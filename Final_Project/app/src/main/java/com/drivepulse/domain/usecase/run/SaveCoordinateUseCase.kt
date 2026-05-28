/**
 * Use case para guardar um ponto GPS na run ativa.
 *
 * Camada: Domain
 * Feature: Run
 *
 * Chamado pelo TrackingForegroundService cada vez que o FusedLocationProvider
 * emite uma nova localização. Delega diretamente ao RunRepository.
 */
package com.drivepulse.domain.usecase.run

import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.repository.RunRepository
import javax.inject.Inject

/**
 * Persiste um [Coordinate] na run identificada por [runId].
 *
 * @param repository repositório de runs injetado via Hilt.
 */
class SaveCoordinateUseCase @Inject constructor(
    private val repository: RunRepository
) {

    /**
     * Invoca o caso de uso.
     *
     * @param runId ID da run que está a ser gravada.
     * @param coordinate ponto GPS capturado pelo FusedLocationProvider.
     */
    suspend operator fun invoke(runId: String, coordinate: Coordinate) {
        repository.addCoordinate(runId = runId, coordinate = coordinate)
    }
}
