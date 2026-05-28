/**
 * UseCase for observing auth state changes (e.g. session expiration, logout).
 *
 * Camada: Domain
 * Feature: Auth
 */
package com.drivepulse.domain.usecase.auth

import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.observeAuthState()
    }
}
