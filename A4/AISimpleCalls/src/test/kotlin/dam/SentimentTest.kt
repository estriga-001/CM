package dam

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.json.JSONObject
import org.json.JSONException
import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * SentimentTest is a JUnit 5 test class designed to verify Task 4: Sentiment Analysis.
 *
 * It tests different inputs ranging from highly positive to highly negative, validating
 * that the AI assistant responds with a properly formatted JSON payload containing the
 * integer rating (1-7 scale) and a justification string.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SentimentTest {

    private lateinit var assistant: AIAssistant
    private val configProperties = Properties()

    @BeforeAll
    fun setUp() {
        // Load the config.properties file from the project's root folder
        val configFile = File("config.properties")
        assertTrue(configFile.exists(), "config.properties must exist in the root folder to run tests.")

        FileInputStream(configFile).use { input ->
            configProperties.load(input)
        }

        // Use AIAssistantFactory to create the AI assistant implementation configured by the user
        assistant = AIAssistantFactory.createAssistant(configProperties)
        println("✅ Sentiment test initialised using assistant system: ${assistant.getSystem()}")
    }

    /**
     * Helper method to validate that the response from analyzeSentiment is a valid JSON
     * matching the schema:
     * {
     *   "rating": value,
     *   "justification": value
     * }
     */
    private fun validateAndParseJson(jsonResponse: String): JSONObject {
        try {
            val jsonObject = JSONObject(jsonResponse)
            
            // Check that the required keys are present
            assertTrue(jsonObject.has("rating"), "JSON response must contain a 'rating' key")
            assertTrue(jsonObject.has("justification"), "JSON response must contain a 'justification' key")
            
            // Validate rating is a number within 1 and 7
            val ratingVal = jsonObject.get("rating")
            assertTrue(ratingVal is Number, "rating must be a numeric value")
            val ratingInt = (ratingVal as Number).toInt()
            assertTrue(ratingInt in 1..7, "rating must be an integer between 1 and 7 (inclusive)")
            
            // Validate justification is a non-empty string
            val justification = jsonObject.get("justification")
            assertTrue(justification is String, "justification must be a string")
            assertTrue((justification as String).trim().isNotEmpty(), "justification must not be empty")
            
            return jsonObject
        } catch (e: JSONException) {
            val errorMsg = "Failed to parse the response as valid JSON. Response was:\n$jsonResponse\nError: ${e.message}"
            fail<JSONObject>(errorMsg)
            throw AssertionError(errorMsg)
        }
    }

    @Test
    fun testVeryPositiveSentiment() = runBlocking {
        val input = "I absolutely love this new update! It is outstanding, works flawlessly, and makes me so incredibly happy!"
        
        println("\n========== Test 1: VERY POSITIVE Sentiment Analysis ==========")
        println("Input text: \"$input\"")
        
        val response = assistant.analyzeSentiment(input)
        println("JSON Response:\n$response")
        
        val parsedJson = validateAndParseJson(response)
        val rating = parsedJson.getInt("rating")
        
        // Very positive should be 6 (Positive) or 7 (Very Positive)
        assertTrue(rating >= 5, "Highly positive sentiment should have a rating of 5, 6 or 7. Got: $rating")
        println("==============================================================")
    }

    @Test
    fun testNeutralSentiment() = runBlocking {
        val input = "The package arrived at 3 PM on Thursday afternoon."
        
        println("\n========== Test 2: NEUTRAL Sentiment Analysis ==========")
        println("Input text: \"$input\"")
        
        val response = assistant.analyzeSentiment(input)
        println("JSON Response:\n$response")
        
        val parsedJson = validateAndParseJson(response)
        val rating = parsedJson.getInt("rating")
        
        // Neutral should ideally be 4 (Neutral), but slightly positive (5) or slightly negative (3) is also acceptable
        assertTrue(rating in 3..5, "Neutral sentiment should have a rating around 4. Got: $rating")
        println("==============================================================")
    }

    @Test
    fun testVeryNegativeSentiment() = runBlocking {
        val input = "This is the absolute worst service I have ever experienced. I am completely disappointed, frustrated, and angry."
        
        println("\n========== Test 3: VERY NEGATIVE Sentiment Analysis ==========")
        println("Input text: \"$input\"")
        
        val response = assistant.analyzeSentiment(input)
        println("JSON Response:\n$response")
        
        val parsedJson = validateAndParseJson(response)
        val rating = parsedJson.getInt("rating")
        
        // Very negative should be 1 (Very Negative) or 2 (Negative)
        assertTrue(rating <= 3, "Highly negative sentiment should have a rating of 1, 2 or 3. Got: $rating")
        println("==============================================================")
    }
}
