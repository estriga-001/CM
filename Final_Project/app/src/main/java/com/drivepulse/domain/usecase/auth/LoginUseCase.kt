/**
 * UseCase for user login.
 *
 * Camada: Domain
 * Feature: Auth
 */
package com.drivepulse.domain.usecase.auth

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AppResult<User> {
        return authRepository.login(email, password)
    }
}
