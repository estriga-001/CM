package tasks

/**
 * Data class representing the loading state of the contributors request.
 *
 * @property isLoading   true while a request is in progress, false otherwise
 * @property progress    optional progress value (0..1) – can be used by the UI to show a progress bar
 * @property message     optional textual description (e.g. "Loading repo 3 of 10")
 */
data class LoadingStateData(
    val isLoading: Boolean = false,
    val progress: Float? = null,
    val message: String? = null
)

/**
 * Simple holder exposing a [StateFlow] of [LoadingStateData]. The UI can collect this flow
 * to receive updates about the loading progress in a lifecycle‑aware way.
 */
object LoadingStateHolder {
    // Backing mutable state – only this file can emit new values.
    private val _state = kotlinx.coroutines.flow.MutableStateFlow(LoadingStateData())

    /**
     * Public read‑only view of the loading state. UI code should use this to observe changes.
     */
    val state: kotlinx.coroutines.flow.StateFlow<LoadingStateData> = _state

    /**
     * Helper to update the loading state. Called from the various request implementations.
     */
    fun update(state: LoadingStateData) {
        _state.value = state
    }
}
