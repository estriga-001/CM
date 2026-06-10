/**
 * ViewModel for Profile Screen.
 *
 * Camada: Presentation
 * Feature: Profile
 *
 * Responsabilidades:
 * - Observar o perfil do utilizador autenticado via Flow.
 * - Carregar os posts publicados pelo utilizador.
 * - Gerir upload de imagem e logout.
 */
package com.drivepulse.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.domain.usecase.auth.LogoutUseCase
import com.drivepulse.domain.usecase.profile.GetUserProfileUseCase
import com.drivepulse.domain.usecase.profile.UpdateUserProfileUseCase
import com.drivepulse.domain.usecase.profile.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Posts publicados pelo utilizador actual. */
    private val _userPosts = MutableStateFlow<AppResult<List<Post>>>(AppResult.Success(emptyList()))
    val userPosts: StateFlow<AppResult<List<Post>>> = _userPosts.asStateFlow()

    private var currentUserId: String? = null

    init {
        observeUser()
    }

    /**
     * Observa o estado de autenticação e carrega o perfil
     * assim que o utilizador estiver autenticado.
     */
    private fun observeUser() {
        viewModelScope.launch {
            authRepository.observeAuthState().collectLatest { authUser ->
                if (authUser != null) {
                    currentUserId = authUser.id
                    loadProfile(authUser.id)
                    loadUserPosts(authUser.id)
                } else {
                    _uiState.value = ProfileUiState.Error("User not authenticated")
                }
            }
        }
    }

    /** Observa o perfil do utilizador em tempo real via Firestore snapshot. */
    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            getUserProfileUseCase(userId).collectLatest { result ->
                when (result) {
                    is AppResult.Success -> {
                        _uiState.value = ProfileUiState.Success(result.data)
                    }
                    is AppResult.Error -> {
                        _uiState.value = ProfileUiState.Error(result.error.message)
                    }
                }
            }
        }
    }

    /** Observa os posts publicados pelo utilizador. */
    private fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            postRepository.getUserPosts(userId).collectLatest { result ->
                _userPosts.value = result
            }
        }
    }

    /** Atualiza o perfil do utilizador no Firestore. */
    fun updateUser(updatedUser: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = updateUserProfileUseCase(updatedUser)
            onComplete(result is AppResult.Success)
        }
    }

    /** Faz upload de uma imagem de perfil comprimida para o Firebase Storage. */
    fun uploadImage(imageBytes: ByteArray, onComplete: (Boolean) -> Unit) {
        val userId = currentUserId
        if (userId == null) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            val result = uploadProfileImageUseCase(userId, imageBytes)
            onComplete(result is AppResult.Success)
        }
    }

    /** Termina a sessão e invoca o callback para navegar ao AuthActivity. */
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLogoutSuccess()
        }
    }
}
