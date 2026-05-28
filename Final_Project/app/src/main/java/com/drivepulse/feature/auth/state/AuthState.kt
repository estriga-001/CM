/**
 * Represents the UI state for the authentication flow.
 *
 * Camada: UI (Presentation)
 * Feature: Auth
 */
package com.drivepulse.feature.auth.state

import com.drivepulse.domain.model.User

/**
 * Sealed interface for Auth UI state.
 */
sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}
