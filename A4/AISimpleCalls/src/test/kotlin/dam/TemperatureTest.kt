package dam

import kotlinx.coroutines.runBlocking
import java.util.Properties

/**
 * TemperatureTest demonstrates how the TEMPERATURE setting in config.properties
 * directly affects the variety and creativity of the AI model's output.
 *
 * Background
 * ----------
 * When an LLM generates text it assigns a probability to every possible next token
 * (word fragment). The temperature parameter scales those probabilities before the
 * model picks a token:
 *
 *   temperature = 0.0  → always picks the single most-likely token  → identical every run
 *   temperature = 0.7  → slight randomness, coherent                → balanced default
 *   temperature = 1.8  → unlikely tokens get a real chance          → very creative / wild
 *
 * What this file contains
 * -----------------------
 * Two test cases that call the Groq API with the SAME prompt but with DIFFERENT
 * temperature values, then print both responses so the difference is visible:
 *
 *   Test 1 — Low  temperature (0.1): factual, predictable, conservative output.
 *   Test 2 — High temperature (1.8): creative, surprising, potentially unusual output.
 *
 * How to run
 * ----------
 *   ./gradlew test                   (runs both tests automatically via JUnit)
 *   ./gradlew test --info            (shows the printed output in the console)
 *
 * Note: these tests make real network calls to the Groq API, so a valid GROQ_API_KEY
 * in config.properties is required.
 */
class TemperatureTest {

    // ─── Shared prompt ────────────────────────────────────────────────────────

    /**
     * The creative writing prompt used by both test cases.
     *
     * Choosing a creative task (rather than a factual question) makes the
     * temperature difference much more pronounced and easier to observe:
     *   - At low  temperature the poem will be generic and predictable.
     *   - At high temperature the poem will be unusual and inventive.
     */
    private val sharedPrompt = "Write a short poem about the sea."

    // ─── Helper ───────────────────────────────────────────────────────────────

    /**
     * Creates a [Properties] object pre-loaded with the Groq API key from
     * the project's config.properties file, then overrides TEMPERATURE with
     * the supplied value.
     *
     * Why override programmatically instead of editing the file?
     * Because test cases must be self-contained and repeatable. Editing a shared
     * config file would affect every other component running at the same time.
     *
     * @param temperatureValue The temperature string to inject, e.g. "0.1" or "1.8".
     * @return A fully configured [Properties] object ready for [AIAssistantGroq].
     */
    private fun buildPropertiesWithTemperature(temperatureValue: String): Properties {
        // Start with the real config file so the API key is loaded correctly.
        // getProperties() is defined in Utils.kt and reads src/main/resources/config.properties.
        val props = getProperties()

        // Override TEMPERATURE with the value we want to test.
        // This call is identical to what would happen if the user edited config.properties —
        // because AIAssistant.temperature reads from this same Properties object at call time.
        props.setProperty("TEMPERATURE", temperatureValue)

        return props // Return the modified properties to the caller
    }

    // ─── Test 1: Low temperature ──────────────────────────────────────────────

    /**
     * Test case 1 — TEMPERATURE = 0.1 (near-deterministic).
     *
     * Expected behaviour:
     *   The model strongly prefers the most likely next token at every step.
     *   Output is conservative, predictable, and would be very similar (or
     *   identical) if the same request were repeated multiple times.
     *
     * This is the right setting for tasks where consistency matters:
     * factual Q&A, code generation, summarisation, translation.
     */
    @org.junit.jupiter.api.Test
    fun testLowTemperature() = runBlocking {
        // ── Arrange ─────────────────────────────────────────────────────────
        val lowTemperature = "0.1" // Near-deterministic; model almost always picks the safest word

        // Build properties with TEMPERATURE overridden to 0.1
        val properties = buildPropertiesWithTemperature(lowTemperature)

        // Create a Groq assistant that will use these properties.
        // AIAssistantGroq.buildRequest() reads `temperature` via AIAssistant.temperature,
        // which calls properties.getProperty("TEMPERATURE") — so it will see "0.1".
        val assistant: AIAssistant = AIAssistantGroq(properties)

        // ── Act ──────────────────────────────────────────────────────────────
        // processInput() calls buildPrompt() -> apiCallWithBackoff() -> makeApiCall()
        // -> buildRequest() (which embeds temperature=0.1 in the JSON payload)
        // -> parseResponse() -> returns the plain text answer.
        val response = assistant.processInput(sharedPrompt)

        // ── Assert / Display ─────────────────────────────────────────────────
        // Print the result so it is visible when running `./gradlew test --info`
        println("\n========== Test 1: LOW temperature ($lowTemperature) ==========")
        println("Prompt  : $sharedPrompt")
        println("Response:\n$response")
        println("=".repeat(60))

        // The only hard assertion is that we received a non-empty string.
        // Comparing creative text for an exact value is impractical, but this
        // confirms the API call completed and returned something meaningful.
        assert(response.isNotBlank()) {
            "Expected a non-blank response from the model at temperature $lowTemperature"
        }
    }

    // ─── Test 2: High temperature ─────────────────────────────────────────────

    /**
     * Test case 2 — TEMPERATURE = 1.8 (highly creative).
     *
     * Expected behaviour:
     *   The model's probability distribution is flattened so that less-likely
     *   tokens are picked much more often. Output is varied, creative, and may
     *   use unusual word choices, unexpected metaphors, or non-standard structure.
     *   Running the same request twice would produce noticeably different poems.
     *
     * This is the right setting for tasks where variety and creativity matter:
     * brainstorming, fiction, poetry, marketing copy.
     *
     * By comparing the output of this test with Test 1 side-by-side, the
     * impact of temperature on the model's behaviour becomes clearly visible.
     */
    @org.junit.jupiter.api.Test
    fun testHighTemperature() = runBlocking {
        // ── Arrange ─────────────────────────────────────────────────────────
        val highTemperature = "1.8" // Highly creative; model frequently picks surprising tokens

        // Build properties with TEMPERATURE overridden to 1.8
        val properties = buildPropertiesWithTemperature(highTemperature)

        // Create a Groq assistant that will use these properties.
        // Same assistant class as Test 1 — the ONLY difference is the TEMPERATURE value.
        val assistant: AIAssistant = AIAssistantGroq(properties)

        // ── Act ──────────────────────────────────────────────────────────────
        // Identical call chain to Test 1; temperature=1.8 is embedded in the JSON payload.
        val response = assistant.processInput(sharedPrompt)

        // ── Assert / Display ─────────────────────────────────────────────────
        println("\n========== Test 2: HIGH temperature ($highTemperature) ==========")
        println("Prompt  : $sharedPrompt")
        println("Response:\n$response")
        println("=".repeat(60))
        println("\n>>> Compare the two responses above to observe the temperature effect.")

        // Same non-blank assertion — the creative output cannot be predicted, but
        // it must be a real non-empty response.
        assert(response.isNotBlank()) {
            "Expected a non-blank response from the model at temperature $highTemperature"
        }
    }
}
