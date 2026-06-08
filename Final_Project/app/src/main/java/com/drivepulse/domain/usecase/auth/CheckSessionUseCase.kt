package com.drivepulse.domain.usecase.auth

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case: Verifica se já existe uma sessão ativa e devolve o perfil do utilizador.
 *
 * Camada: Domain
 * Feature: Auth
 */
class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): AppResult<User?> {
        return authRepository.checkCurrentSession()
    }
}
