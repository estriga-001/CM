/**
 * Interface do LocationTracker — abstração para o FusedLocationProviderClient.
 *
 * Camada: Core / Location
 * Feature: Run
 *
 * Permite substituir a implementação real por um mock nos testes unitários.
 */
package com.drivepulse.core.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para rastreio de localização GPS.
 *
 * Implementado por [FusedLocationTracker] com a API real do Google Play Services.
 * Em testes, pode ser substituído por uma implementação fake.
 */
interface LocationTracker {

    /**
     * Inicia a subscrição de atualizações de localização e retorna um [Flow] reativo.
     *
     * O Flow emite uma [Location] por cada atualização do FusedLocationProvider.
     * O Flow termina quando [stopTracking] é chamado ou o scope do coletor é cancelado.
     */
    fun getLocationUpdates(): Flow<Location>
}
