/**
 * ViewModel para o ecrã de criação/publicação de posts.
 *
 * Camada: Presentation
 * Feature: CreatePost
 *
 * Responsabilidades:
 * - Carregar os dados da run local (se existir runId).
 * - Gerir o estado do formulário (descrição, media).
 * - Publicar o post no Firestore via PostRepository.
 */
package com.drivepulse.feature.createpost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.model.MediaType
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.domain.repository.RunRepository
import com.drivepulse.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * Estado da UI do ecrã de criação de posts.
 */
data class CreatePostUiState(
    val isLoading: Boolean = true,
    val description: String = "",
    val mediaBytes: ByteArray? = null,
    val selectedTags: Set<String> = emptySet(),
    val runId: String? = null,
    val distanceMeters: Float = 0f,
    val durationSeconds: Long = 0L,
    val avgSpeedKmh: Float = 0f,
    val runCoordinates: List<Coordinate> = emptyList(),
    val isPublishing: Boolean = false,
    val isPublished: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val runRepository: RunRepository,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    /** O runId vem do argumento de navegação. */
    private val runId: String = savedStateHandle.get<String>("runId") ?: ""

    init {
        loadRunData()
    }

    /**
     * Carrega os dados da run local do Room para preview no ecrã.
     * Se o runId estiver vazio, assume um post sem run associada.
     */
    private fun loadRunData() {
        if (runId.isBlank()) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            try {
                val run = runRepository.getRunById(runId).firstOrNull()
                if (run != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            runId = run.id,
                            distanceMeters = run.distanceMeters,
                            durationSeconds = run.durationSeconds,
                            avgSpeedKmh = run.avgSpeedKmh,
                            runCoordinates = run.coordinates
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Run não encontrada.") }
                }
            } catch (e: Exception) {
                Timber.e(e, "Erro ao carregar dados da run")
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar a run.") }
            }
        }
    }

    /** Atualiza a descrição do post no estado. */
    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    /** Define os bytes da imagem selecionada pelo utilizador. */
    fun onMediaSelected(bytes: ByteArray?) {
        _uiState.update { it.copy(mediaBytes = bytes) }
    }

    fun onTagToggled(tag: String) {
        _uiState.update { currentState ->
            val updatedTags = if (tag in currentState.selectedTags) {
                currentState.selectedTags - tag
            } else {
                currentState.selectedTags + tag
            }
            currentState.copy(selectedTags = updatedTags)
        }
    }

    /**
     * Publica o post no Firestore.
     * Recolhe os dados do utilizador atual, constrói o Post e delega ao PostRepository.
     */
    fun publish() {
        val currentUser = firebaseAuth.currentUser ?: return
        val state = _uiState.value

        _uiState.update { it.copy(isPublishing = true, error = null) }

        viewModelScope.launch {
            try {
                // Obter dados de perfil do utilizador para denormalizar no post
                val userResult = userRepository.getUserProfile(currentUser.uid).firstOrNull()
                val user = (userResult as? AppResult.Success)?.data

                val post = Post(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser.uid,
                    username = user?.username ?: currentUser.displayName ?: "anon",
                    userProfileImage = user?.profileImageUrl,
                    description = state.description.trim(),
                    runId = state.runId,
                    distanceMeters = state.distanceMeters,
                    durationSeconds = state.durationSeconds,
                    avgSpeedKmh = state.avgSpeedKmh,
                    runCoordinates = state.runCoordinates,
                    mediaUrl = null, // Será preenchido pelo PostRepositoryImpl após upload
                    mediaType = if (state.mediaBytes != null) MediaType.IMAGE else null,
                    tags = state.selectedTags.sorted(),
                    createdAt = System.currentTimeMillis()
                )

                val result = postRepository.createPost(post, state.mediaBytes)
                when (result) {
                    is AppResult.Success -> {
                        _uiState.update { it.copy(isPublishing = false, isPublished = true) }
                        Timber.d("✅ Post publicado com sucesso")
                    }
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isPublishing = false, error = result.error.message) }
                        Timber.e("❌ Erro ao publicar post: ${result.error.message}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "❌ Erro inesperado ao publicar post")
                _uiState.update { it.copy(isPublishing = false, error = "Erro inesperado. Tenta novamente.") }
            }
        }
    }
}
