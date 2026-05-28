/**
 * Use case para finalizar uma run gravada.
 *
 * Camada: Domain
 * Feature: Run
 *
 * Calcula a velocidade média a partir da distância e duração e persiste
 * os dados finais via RunRepository. Mantém status DRAFT até publicação (Fase 4).
 */
package com.drivepulse.domain.usecase.run

import com.drivepulse.domain.repository.RunRepository
import javax.inject.Inject

/**
 * Finaliza uma run, persistindo duração, distância e velocidade média.
 *
 * @param repository repositório de runs injetado via Hilt.
 */
class FinishRunUseCase @Inject constructor(
    private val repository: RunRepository
) {

    /**
     * Invoca o caso de uso.
     *
     * @param runId ID da run a finalizar.
     * @param durationSeconds duração total medida pelo cronómetro do ViewModel.
     * @param distanceMeters distância total acumulada em metros.
     */
    suspend operator fun invoke(
        runId: String,
        durationSeconds: Long,
        distanceMeters: Float
    ) {
        // Calcula velocidade média em km/h a partir da distância e duração
        val avgSpeedKmh = if (durationSeconds > 0) {
            (distanceMeters / 1000f) / (durationSeconds / 3600f)
        } else {
            0f
        }

        repository.finishRun(
            runId = runId,
            durationSeconds = durationSeconds,
            distanceMeters = distanceMeters,
            avgSpeedKmh = avgSpeedKmh
        )
    }
}
