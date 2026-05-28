/**
 * Use case para partilhar uma rota com a comunidade.
 *
 * Camada: Domain
 * Feature: Run / Community
 */
package com.drivepulse.domain.usecase.run

import com.drivepulse.domain.repository.AuthRepository
import com.drivepulse.domain.repository.RunRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PublishRunUseCase @Inject constructor(
    private val runRepository: RunRepository,
    private val authRepository: AuthRepository
) {
    /**
     * @param runId ID da rota local a ser publicada
     * @return Result.success se for publicada com sucesso, ou Result.failure em caso de erro.
     */
    suspend operator fun invoke(runId: String): Result<Unit> {
        return try {
            // Obter o nome do utilizador logado para associar à rota (ou fallback)
            val user = authRepository.observeAuthState().firstOrNull()
            val userName = user?.username?.ifBlank { "Condutor Anónimo" } ?: "Condutor Anónimo"
            
            runRepository.publishRun(runId, userName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
