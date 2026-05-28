/**
 * UseCase for logging out.
 *
 * Camada: Domain
 * Feature: Auth
 */
package com.drivepulse.domain.usecase.auth

import com.drivepulse.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
