package dam

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

/**
 * GeminiAIAssistant class provides an interface to communicate with Google's Gemini AI models.
 * This class handles API authentication, request formatting, response parsing, and error handling.
 * It implements retry logic for rate-limited requests and validates JSON responses.
 *
 * @param properties Properties containing the API key for authentication with Gemini services
 */
class AIAssistantGeminiClasses(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "GEMINI"
    override val apiKeyName = "GEMINI_API_KEY"

    // Models available on this API key (2.0+ generation only)
    // NOTE: gemini-1.5-* models are NOT available on this key
     override var model = "gemini-2.0-flash-lite" // Lightest 2.x model
    // override var model = "gemini-2.0-flash"      // Full 2.0 (may need billing)
    // override var model = "gemini-2.5-flash"      // Most capable available

    // Data classes for Gemini API request structure

    data class Part(
        val text: String
    )

    data class Content(
        val role: String,
        val parts: List<Part>
    )

    data class GeminiRequest(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null
    )

    data class GenerationConfig(
        val temperature: Double? = 0.4,      // Default reasonable balance
        val topK: Int? = 40,                 // Limits selection to top K most likely tokens
        val topP: Double? = 0.95,            // Nucleus sampling - covers 95% of probability mass
        val maxOutputTokens: Int? = 800,     // Controls response length
        val candidateCount: Int? = 1         // Number of alternative responses to generate
    )

    data class GeminiResponse(val candidates: List<Candidate>)
    data class Candidate(val content: ContentResponse)
    data class ContentResponse(val parts: List<Part>)

    // 2. Add the parsing method
    override fun parseResponse(responseBody: String): String {
        val response = gson.fromJson(responseBody, GeminiResponse::class.java)
        return response.candidates[0].content.parts[0].text.trim()
    }


    // Gson instance for JSON serialization
    private val gson = Gson()

    /**
     * Constructs and formats a structured request from the given input prompt.
     * This method is intended to prepare the necessary request structure for
     * sending to an AI-powered model or API.
     *
     * @param prompt The user's input query or prompt that needs to be formatted into a request
     */
    override fun buildRequest(prompt: String): Request {
        // Create request structure using data classes
        val part = Part(text = prompt)
        val content = Content(
            role = "user",
            parts = listOf(part)
        )
        val geminiRequest = GeminiRequest(
            contents = listOf(content),
            generationConfig = GenerationConfig(
                temperature = temperature,     // Sourced from AIAssistant.temperature -> config.properties TEMPERATURE
                maxOutputTokens = maxTokens    // Sourced from AIAssistant.maxTokens -> config.properties MAX_TOKENS
            )
        )

        // Convert to JSON string using Gson
        val requestBody = gson.toJson(geminiRequest)

        // All current Gemini models use the v1beta endpoint
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        return request
    }



}