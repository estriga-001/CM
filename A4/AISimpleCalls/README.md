# 🤖 LLM Assistant Application (`AISimpleCalls`)

A robust, premium Kotlin command-line interface designed to demonstrate seamless integration with multiple modern LLM providers. The application supports modular extensions, configurable generation parameters, structured logging, 7-point scale sentiment analysis, and automated parameter validation.

---

## ✨ Features

- **Multi-Provider Support**: Seamlessly switch between **Groq** (free tier with no credit card required), **OpenAI**, and **Google Gemini** using a unified interface.
- **Configurable Generation Parameters**: Fine-tune model behavior directly from the `config.properties` file using `TEMPERATURE` and `MAX_TOKENS`.
- **Base-Delegated Fallbacks**: Smart fallback mechanisms in the interface ensure the application works flawlessly even if properties are omitted or incorrectly formatted.
- **7-Point Sentiment Analysis**: Built-in polymorphic sentiment processing engine returning structured JSON payloads with a numerical rating and detailed text justification.
- **Robust Network Layer**: Equipped with exponential backoff retries for rate-limiting errors (`429 Too Many Requests`).
- **Interactive Chat CLI**: A fully-fledged command-line loop supporting persistent interactions and clean exit states.
- **Side-by-Side Temperature Tests**: Automated JUnit 5 tests demonstrating the impact of temperature levels (`0.1` vs `1.8`) on model output variety.

---

## 🛠️ Configuration (`config.properties`)

The application is controlled by the `config.properties` file in the root directory.

```properties
# ── API Keys ──────────────────────────────────────────────────────────────────
OPENAI_API_KEY=your-openai-key
GEMINI_API_KEY=your-gemini-key
GROQ_API_KEY=your-groq-key

# ── LLM Service Selection ─────────────────────────────────────────────────────
# Options: GROQ (Recommended free tier), OPENAI, GEMINI, OPENAI-CLASSES, GEMINI-CLASSES
AI_LLM=GROQ

# ── Structured Logging ────────────────────────────────────────────────────────
# Options: OFF, ERROR, WARN, INFO, DEBUG, TRACE
LOG_LEVEL=OFF

# ── Generation Parameters (Optional) ──────────────────────────────────────────
# TEMPERATURE: Randomness control (0.0 = deterministic, 2.0 = highly creative)
# Fallback: 0.7 if absent or invalid.
TEMPERATURE=0.7

# MAX_TOKENS: Maximum response length constraint (number of output tokens)
# Fallback: 800 if absent or invalid.
MAX_TOKENS=800
```

---

## 🚀 Quickstart Commands

| Action | PowerShell (Windows) | Command Prompt (cmd) | Bash (Linux/macOS) |
| :--- | :--- | :--- | :--- |
| **Run Interactive Chat** | `.\gradlew run` | `gradlew run` | `./gradlew run` |
| **Run All Tests** | `.\gradlew cleanTest test --info` | `gradlew cleanTest test --info` | `./gradlew cleanTest test --info` |
| **Run Temperature Tests Only**| `.\gradlew cleanTest test --tests "dam.TemperatureTest" --info` | `gradlew cleanTest test --tests "dam.TemperatureTest" --info` | `./gradlew cleanTest test --tests "dam.TemperatureTest" --info` |
| **Run Sentiment Tests Only**  | `.\gradlew cleanTest test --tests "dam.SentimentTest" --info` | `gradlew cleanTest test --tests "dam.SentimentTest" --info` | `./gradlew cleanTest test --tests "dam.SentimentTest" --info` |
| **Clean & Rebuild** | `.\gradlew clean build` | `gradlew clean build` | `./gradlew clean build` |

> [!IMPORTANT]
> The `--info` flag is required when running tests to output the generated response side-by-side in your console.
> The `cleanTest` task is recommended to prevent Gradle from skipping test execution due to its "UP-TO-DATE" build cache.

---

## 🧠 LLM Temperature Explained

**Temperature** changes how the AI selects its next word:

*   **Low Temperature (`0.1` - `0.3`)**: More deterministic, safe, focused, and predictable. The model always picks the single most probable token. Excellent for factual Q&A, translation, and code generation.
*   **High Temperature (`1.5` - `1.8`)**: Highly creative, surprising, and varied. Probability is flattened to allow unlikely tokens to be selected. Excellent for brainstorming, fiction, and poetry.

### How to Verify the Temperature Effect
We have implemented an automated JUnit test in `src/test/kotlin/dam/TemperatureTest.kt`. It sends the prompt *"Write a short poem about the sea."* twice, overriding `TEMPERATURE` programmatically to `0.1` and `1.8` respectively.

Running `.\gradlew cleanTest test --tests "dam.TemperatureTest" --info` yields:
1.  **At `0.1` (Low)**: The model responds with traditional structure, conversational introductions ("Hello, I'm Assistant. I'd be delighted to..."), and a safe rhyme scheme.
2.  **At `1.8` (High)**: The model dives straight into a completely different poem, omitting introductory dialog entirely, and utilizing unique metaphors.

---

## 📊 Sentiment Analysis (Task 4)

We have built a 7-point scale sentiment classifier natively inside the `AIAssistant` interface:
1. **Very Negative**
2. **Negative**
3. **Slightly Negative**
4. **Neutral**
5. **Slightly Positive**
6. **Positive**
7. **Very Positive**

The AI evaluates any user input text and strictly returns a standard JSON object containing a numeric rating key and a string justification key:
```json
{
  "rating": 7,
  "justification": "The text contains extremely positive language, including 'absolutely love' and 'outstanding', indicating a very strong positive sentiment."
}
```

### How to Run Sentiment Verification
We created `src/test/kotlin/dam/SentimentTest.kt` which tests extremely positive, completely neutral, and extremely negative user strings. Run:
```powershell
.\gradlew cleanTest test --tests "dam.SentimentTest" --info
```

---

## 📂 Project Architecture

*   [`AIAssistant.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/main/kotlin/dam/AIAssistant.kt): Base interface containing standard properties, exponential backoff logic, and the new config-driven `temperature` and `maxTokens` implementations, as well as the new `analyzeSentiment(input: String)` method.
*   [`AIAssistantGroq.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/main/kotlin/dam/AIAssistantGroq.kt): OpenAI-compatible client targeting Groq completions endpoints.
*   [`AIAssistantOpenAI.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/main/kotlin/dam/AIAssistantOpenAI.kt) / [`AIAssistantGemini.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/main/kotlin/dam/AIAssistantGemini.kt): Basic integrations.
*   [`AIAssistantOpenAIClasses.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/main/kotlin/dam/AIAssistantOpenAIClasses.kt) / [`AIAssistantGeminiClasses.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/main/kotlin/dam/AIAssistantGeminiClasses.kt): GSON data-class request and response structures.
*   [`TemperatureTest.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/test/kotlin/dam/TemperatureTest.kt): JUnit 5 test demonstrating temperature-driven behavior variation.
*   [`SentimentTest.kt`](file:///d:/CM/CM/A4/AISimpleCalls/src/test/kotlin/dam/SentimentTest.kt): JUnit 5 test verifying 7-point scale sentiment analysis and JSON formatting.