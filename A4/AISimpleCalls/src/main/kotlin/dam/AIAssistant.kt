package dam

import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties
import kotlin.math.pow

/**
 * AIAssistant interface defines the contract for different AI assistant implementations.
 * Any class implementing this interface must provide methods for processing user input
 * and retrieving model information.
 */
interface AIAssistant {

    /**
     * Represents the configuration and system properties used within the AI Assistant.
     * This properties object is used to load and store key-value pairs necessary for
     * configuring the assistant, such as API keys, logging levels, and other application settings.
     */
    val properties: Properties

    /**
     * Logger instance to enable structured and consistent logging within the `AIAssistant` class.
     * This employs SLF4J to dynamically bind to an underlying logging framework such as Logback,
     * ensuring compatibility across different runtime environments.
     *
     * The logger is initialized lazily to capture the class name of the containing class (`AIAssistant`)
     * for all log messages, providing proper context in debugging and service monitoring scenarios.
     *
     * Use this logger for error reporting, debugging, and informational logs throughout the
     * `AIAssistant` implementation and its associated methods.
     */
    val logger: Logger
        get() = LoggerFactory.getLogger(this::class.java)

    /**
     * Represents the name of the API key used for authentication.
     * This property holds the identifier for the API key required to interact with external services.
     */
    val apiKeyName: String

    /**
     * The AI model being used
     * This property should be set by implementing classes
     */
    var model: String


    /**
     * Provides an instance of OkHttpClient used for making HTTP requests.
     * The client is lazily initialized and is intended for use in network
     * operations, such as API calls within the assistant's functionality.
     *
     * This instance is reusable and helps manage HTTP connections efficiently.
     */
    val client: OkHttpClient
        get() = OkHttpClient()

    /**
     * Represents the API key used to authenticate requests to an external service.
     *
     * The value is dynamically retrieved from the system's configuration properties
     * using the `apiKeyName`. If the key is not found, an exception is thrown to prevent
     * unauthenticated requests.
     *
     * @throws IllegalStateException If the API key is not defined in the configuration file
     */
    val apiKey: String
        get() = properties.getProperty(apiKeyName)
            ?: throw IllegalStateException("API key $apiKeyName not found in configuration file.")

    /**
     * Temperature controls the randomness ("creativity") of the model's output.
     *
     * How it works: the model assigns a probability to every possible next token.
     * Temperature then scales those probabilities before the model makes its pick:
     *   - 0.0 → always picks the single most-likely token  → perfectly deterministic
     *   - 0.7 → slight randomness, still coherent           → balanced default
     *   - 2.0 → unlikely tokens get a real chance           → very creative / unpredictable
     *
     * This value is read from the TEMPERATURE key in config.properties.
     * If the key is missing or cannot be parsed as a Double, the fallback 0.7 is used,
     * which keeps existing behaviour unchanged when the property is not configured.
     *
     * Used by: AIAssistantGroq.buildRequest(), AIAssistantGeminiClasses.buildRequest(),
     *          AIAssistantOpenAIClasses.buildRequest()
     */
    val temperature: Double
        // getProperty returns null when key is absent; toDoubleOrNull returns null when
        // the value cannot be parsed (e.g. typo). The ?: operator supplies the fallback.
        get() = properties.getProperty("TEMPERATURE")?.toDoubleOrNull() ?: 0.7

    /**
     * Maximum number of tokens the model is allowed to generate in a single response.
     *
     * A "token" is roughly 3/4 of an English word (so 800 tokens ≈ 600 words).
     * Capping this value:
     *   - Prevents unexpectedly long (and slow) responses.
     *   - Reduces API quota consumption on metered services.
     *
     * This value is read from the MAX_TOKENS key in config.properties.
     * If the key is missing or cannot be parsed as an Int, the fallback 800 is used,
     * which keeps existing behaviour unchanged when the property is not configured.
     *
     * Used by: AIAssistantGroq.buildRequest(), AIAssistantGeminiClasses.buildRequest(),
     *          AIAssistantOpenAIClasses.buildRequest()
     */
    val maxTokens: Int
        // Same null-safe pattern as temperature above.
        get() = properties.getProperty("MAX_TOKENS")?.toIntOrNull() ?: 800


    /**
     * Returns the name/identifier of the system being used
     *
     * @return String representing the system name
     */
    fun getSystem(): String

    /**
     * Processes user input by building a formatted prompt and making an API call.
     * This method provides a clean interface for external components to interact with the assistant.
     * It handles the entire process from raw user input to final response.
     *
     * @param input The raw user input to process
     * @return The model's response as a string
     * @throws Exception If API call fails or response processing fails
     */
    suspend fun processInput(input: String): String {
        // Format the raw input using the buildPrompt method
        val formattedPrompt = buildPrompt(input)

        // Make the API call with the formatted prompt
        return apiCallWithBackoff(formattedPrompt)
    }

    /**
     * Performs sentiment analysis on the given input text.
     * Evaluates the sentiment on a 7-point scale as follows:
     *   1. Very Negative
     *   2. Negative
     *   3. Slightly Negative
     *   4. Neutral
     *   5. Slightly Positive
     *   6. Positive
     *   7. Very Positive
     *
     * The response must be formatted as a JSON object containing the rating number and a justification:
     * {
     *   "rating": value,
     *   "justification": "value"
     * }
     *
     * @param input The raw text to evaluate
     * @return The JSON formatted response from the AI assistant
     */
    suspend fun analyzeSentiment(input: String): String {
        // Build a specialised prompt directing the LLM to perform sentiment evaluation and output strictly JSON.
        val sentimentPrompt = """
            Perform sentiment analysis on the following text.
            Evaluate the sentiment on a 7-point scale as follows:
            1. Very Negative
            2. Negative
            3. Slightly Negative
            4. Neutral
            5. Slightly Positive
            6. Positive
            7. Very Positive

            You MUST respond ONLY with a JSON object in this exact format:
            {
              "rating": <integer value between 1 and 7>,
              "justification": "<brief explanation in English of why you chose this rating>"
            }
            Do not include any other text, markdown formatting (like ```json), or conversational preamble.

            Text to analyze: "$input"
        """.trimIndent()

        // Call the underlying API model with exponential backoff mechanism
        return apiCallWithBackoff(sentimentPrompt)
    }

    /**
     * Builds a structured prompt for the Gemini model with consistent instructions.
     * This ensures the model responds predictably with a consistent personality.
     *
     * @param input The user's input query
     * @return A formatted prompt string with system instructions and user query
     */
    fun buildPrompt(input: String): String {
        return """
            Your name is Assistant.
            The preferred language is English.
            Respond in a friendly and helpful manner.
            The user's request is: "$input"
            """.trimIndent()
    }

    /**
     * Calls the Gemini API with an exponential backoff retry mechanism.
     * This method will automatically retry failed requests due to rate limiting (HTTP 429),
     * implementing an exponential backoff strategy to avoid overwhelming the API.
     *
     * @param input User's input query to send to the Gemini API
     * @return The model's response as a string
     * @throws Exception If the maximum retry attempts are exceeded or other error occurs
     */
    suspend fun apiCallWithBackoff(input: String): String {
        var attempts = 0
        val maxAttempts = 5  // Maximum number of retry attempts
        val baseDelay = 1000L  // Base delay in milliseconds (1 second)

        while (attempts < maxAttempts) {
            try {
                // Attempt to call the Gemini API
                return makeApiCall(input)

            } catch (e: Exception) {
                println("   ⚠️ API error (attempt $attempts): ${e.message}")
                logger.error("Error message: ${e.message}")

                // Only retry on rate-limiting errors (HTTP 429)
                if (e.message?.contains("429") == true) {
                    logger.warn("Error 429: Too Many Requests. Will delay and retry.")
                    attempts++

                    // Calculate exponential backoff delay: baseDelay * 2^attempts
                    val delayTime = baseDelay * (2.0.pow(attempts.toDouble())).toLong()
                    logger.info("Attempt: $attempts failed - will delay: $delayTime ms")
                    println("   🔁 Retrying in ${delayTime}ms (attempt $attempts/$maxAttempts)...")
                    delay(delayTime)
                } else {
                    // For other errors, propagate them immediately without a retry
                    throw e
                }
            }
        }
        // If we've exhausted all retry attempts, throw an exception
        throw Exception("Exceeded maximum retry attempts")
    }

    /**
     * Makes an API call with the provided prompt and processes the response.
     * This method builds a request, sends it using an HTTP client, and extracts
     * the content from the response, validating its structure and handling errors.
     *
     * @param prompt The query or input text to send to the API.
     * @return The processed response text extracted from the API's response.
     *         Returns an error message if the response content is invalid.
     * @throws Exception If the API call fails or the response cannot be processed.
     */
    fun makeApiCall(prompt: String): String {
        logger.info("Prompt:\n$prompt")
        val request = buildRequest(prompt)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw Exception("Error: ${response.code} - ${response.message}\n$errorBody")
            }
            val responseBody = response.body?.string() ?: return "Error: empty response"
            return parseResponse(responseBody) // Use the new abstract method
        }
    }

    fun parseResponse(responseBody: String): String

    /**
     * Constructs and formats a structured request from the given input prompt.
     * This method is intended to prepare the necessary request structure for
     * sending to an AI-powered model or API.
     *
     * @param prompt The user's input query or prompt that needs to be formatted into a request
     */
    fun buildRequest(prompt: String): Request

}

///**
// * AIAssistantFactory creates the appropriate AIAssistant implementation
// * based on configuration settings in the provided Properties object.
// */
//class AIAssistantFactory {
//    companion object {
//        /**
//         * Creates and returns an AIAssistant based on configuration
//         *
//         * @param properties Configuration properties containing API keys and settings
//         * @return An implementation of AIAssistant (either OpenAI or Gemini)
//         */
//        fun createAssistant(properties: Properties): AIAssistant {
//            // Determine which assistant to create based on configuration
//            return when (properties.getProperty("AI_LLM", "OPENAI")) {
//                "OPENAI" -> AIAssistantOpenAI(properties)
//                "GEMINI" -> AIAssistantGemini(properties)
//                "OPENAI-CLASSES" -> AIAssistantOpenAIClasses(properties)
//                "GEMINI-CLASSES" -> AIAssistantGeminiClasses(properties)
//                else -> throw IllegalArgumentException("Invalid AI model type specified in configuration. Valid values are 'OPENAI' or 'GEMINI'.")
//            }
//        }
//    }
//}
