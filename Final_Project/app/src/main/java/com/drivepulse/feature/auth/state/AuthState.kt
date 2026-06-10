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
    /** Resultado de um login/registo activo feito pelo utilizador. */
    data class Success(val user: User) : AuthState
    /** Sessão já existia ao arrancar a app — navegar directamente sem mostrar formulário. */
    data class SessionRestored(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}
