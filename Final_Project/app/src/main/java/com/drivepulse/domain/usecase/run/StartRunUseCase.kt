/**
 * Use case para iniciar uma nova run de condução.
 *
 * Camada: Domain
 * Feature: Run
 *
 * Cria um registo DRAFT no repositório local e retorna o ID gerado.
 * Não conhece Room nem Firebase — usa apenas a interface [RunRepository].
 */
package com.drivepulse.domain.usecase.run

import com.drivepulse.domain.repository.RunRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Cria uma nova run em modo DRAFT e retorna o seu ID único.
 *
 * O ID é um UUID gerado no momento da chamada, garantindo unicidade offline.
 * A run é persistida localmente e pode ser publicada na Fase 4 (Firestore).
 *
 * @param repository repositório de runs injetado via Hilt.
 */
class StartRunUseCase @Inject constructor(
    private val repository: RunRepository
) {

    /**
     * Invoca o caso de uso.
     *
     * @param userId ID do utilizador que inicia a run (pode estar autenticado ou ser guest).
     * @param title título opcional para a run.
     * @return o [runId] (UUID String) da run recém-criada.
     */
    suspend operator fun invoke(
        userId: String,
        title: String = "Run ${System.currentTimeMillis()}"
    ): String {
        return repository.createRun(userId = userId, title = title)
    }
}
