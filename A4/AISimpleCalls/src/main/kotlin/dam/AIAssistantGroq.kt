package dam

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

/**
 * AIAssistantGroq provides an interface to Groq's inference API.
 *
 * Groq is compatible with the OpenAI chat/completions format, so the request
 * and response data classes are identical — only the base URL and API key differ.
 *
 * Free tier: no credit card required. Sign up at https://console.groq.com
 *
 * @param properties Properties containing GROQ_API_KEY and optional settings.
 */
class AIAssistantGroq(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "GROQ"
    override val apiKeyName = "GROQ_API_KEY"

    // Available free models on Groq (no credit card required):
     override var model = "llama-3.3-70b-versatile"
    // override var model = "llama-3.1-8b-instant"
    // override var model = "mixtral-8x7b-32768"
    // override var model = "gemma2-9b-it"

    // Groq uses the same request/response format as OpenAI
    data class Message(val role: String, val content: String)

    /**
     * Payload sent to the Groq inference endpoint.
     *
     * @param model       The Groq model identifier (e.g. "llama-3.3-70b-versatile").
     *                    Supplied by the `model` property defined in this class.
     * @param messages    Conversation history — at minimum a system prompt + user message.
     * @param temperature Controls output randomness. Read from config.properties via
     *                    AIAssistant.temperature. Range: 0.0 (deterministic) – 2.0 (creative).
     *                    No default is set here; the caller always provides the value
     *                    explicitly so that config.properties is always respected.
     * @param max_tokens  Maximum number of tokens the model may generate.
     *                    Read from config.properties via AIAssistant.maxTokens.
     *                    Uses snake_case to match the exact JSON key the Groq API expects.
     */
    data class GroqRequest(
        val model: String,          // Which model to call (set by the `model` override above)
        val messages: List<Message>,// The prompt history sent with every request
        val temperature: Double,    // Randomness level — sourced from AIAssistant.temperature
        val max_tokens: Int         // Max response length — sourced from AIAssistant.maxTokens
    )
    data class GroqResponse(val choices: List<Choice>)
    data class Choice(val message: MessageResponse)
    data class MessageResponse(val content: String)

    private val gson = Gson()

    /**
     * Builds an HTTP request targeting the Groq inference endpoint.
     * The payload format is identical to OpenAI's chat/completions API.
     *
     * Flow:
     *  1. Assemble the message list (system instruction + user prompt).
     *  2. Create a GroqRequest, pulling temperature and max_tokens from
     *     the interface properties (which read config.properties).
     *  3. Serialise the request to JSON using Gson.
     *  4. Wrap it in an OkHttp Request with the required headers.
     *
     * @param prompt The formatted prompt produced by AIAssistant.buildPrompt().
     * @return An OkHttp Request ready to be executed by AIAssistant.makeApiCall().
     */
    override fun buildRequest(prompt: String): Request {
        // Build the message list:
        //   - The system message sets the assistant's persona for the entire conversation.
        //   - The user message contains the actual prompt produced by buildPrompt().
        val messages = listOf(
            Message(role = "system", content = "You are a friendly and helpful assistant."),
            Message(role = "user",   content = prompt)
        )

        // Construct the request payload.
        // `temperature` and `max_tokens` are NOT hardcoded here; they are delegated to
        // the interface properties AIAssistant.temperature and AIAssistant.maxTokens,
        // which read TEMPERATURE and MAX_TOKENS from config.properties (with safe fallbacks).
        val groqRequest = GroqRequest(
            model       = model,        // Model set by the `model` override at the top of the class
            messages    = messages,     // System + user messages assembled above
            temperature = temperature,  // From AIAssistant.temperature -> config.properties TEMPERATURE
            max_tokens  = maxTokens     // From AIAssistant.maxTokens  -> config.properties MAX_TOKENS
        )

        // Serialise the data class to a JSON string that the Groq API understands
        val requestBody = gson.toJson(groqRequest)

        // Build and return the final HTTP POST request:
        //   - URL           : Groq's OpenAI-compatible chat completions endpoint
        //   - Authorization : Bearer token using the API key from config.properties
        //   - Content-Type  : tell the server we are sending JSON
        return Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions") // Groq endpoint (OpenAI-compatible)
            .addHeader("Authorization", "Bearer $apiKey")           // apiKey -> AIAssistant.apiKey -> GROQ_API_KEY in config
            .addHeader("Content-Type", "application/json")          // Body format declaration
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull())) // Attach JSON body
            .build()
    }

    override fun parseResponse(responseBody: String): String {
        val response = gson.fromJson(responseBody, GroqResponse::class.java)
        return response.choices[0].message.content.trim()
    }
}
