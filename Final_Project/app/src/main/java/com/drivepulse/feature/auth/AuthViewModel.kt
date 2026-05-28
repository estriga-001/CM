/**
 * ViewModel for Authentication (Login and Register).
 *
 * Camada: UI (Presentation)
 * Feature: Auth
 */
package com.drivepulse.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.usecase.auth.LoginUseCase
import com.drivepulse.domain.usecase.auth.RegisterUseCase
import com.drivepulse.domain.usecase.auth.GoogleSignInUseCase
import com.drivepulse.feature.auth.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (!validateInputs(email, password)) return

        _uiState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = loginUseCase(email, password)) {
                is AppResult.Success -> {
                    _uiState.value = AuthState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = AuthState.Error(result.error.message)
                }
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        if (!validateInputs(email, password)) return
        
        if (password != confirmPassword) {
            _uiState.value = AuthState.Error("Passwords do not match.")
            return
        }

        _uiState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = registerUseCase(email, password)) {
                is AppResult.Success -> {
                    _uiState.value = AuthState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = AuthState.Error(result.error.message)
                }
            }
        }
    }

    fun googleSignIn(idToken: String) {
        _uiState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = googleSignInUseCase(idToken)) {
                is AppResult.Success -> {
                    _uiState.value = AuthState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = AuthState.Error(result.error.message)
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthState.Idle
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthState.Error("Fields cannot be empty.")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthState.Error("Invalid email format.")
            return false
        }
        if (password.length < 6) {
            _uiState.value = AuthState.Error("Password must be at least 6 characters.")
            return false
        }
        return true
    }
}
