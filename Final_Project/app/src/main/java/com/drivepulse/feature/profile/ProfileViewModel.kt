/**
 * ViewModel for Profile Screen.
 *
 * Camada: UI
 * Feature: Profile
 */
package com.drivepulse.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
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
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.observeAuthState().collectLatest { authUser ->
                if (authUser != null) {
                    currentUserId = authUser.id
                    loadProfile(authUser.id)
                } else {
                    _uiState.value = ProfileUiState.Error("User not authenticated")
                }
            }
        }
    }

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

    fun updateUser(updatedUser: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = updateUserProfileUseCase(updatedUser)
            onComplete(result is AppResult.Success)
        }
    }

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

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLogoutSuccess()
        }
    }
}
