/**
 * UseCase for user registration.
 *
 * Camada: Domain
 * Feature: Auth
 */
package com.drivepulse.domain.usecase.auth

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AppResult<User> {
        return authRepository.register(email, password)
    }
}
