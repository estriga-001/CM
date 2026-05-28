/**
 * Interface para a fonte de dados remota (Cloud).
 *
 * Camada: Data (Remote)
 * Feature: Community / Sync
 */
package com.drivepulse.data.remote.datasource

import com.drivepulse.domain.model.Run
import kotlinx.coroutines.flow.Flow

/**
 * Contrato abstrato para interagir com o backend cloud (Firestore)
 * para a partilha e leitura de runs globais da comunidade.
 */
interface RunRemoteDataSource {
    
    /**
     * Envia uma [Run] local para a cloud.
     * @param run a rota a publicar.
     * @param userName nome do utilizador que publica (para cache visual).
     */
    suspend fun publishRun(run: Run, userName: String)
    
    /**
     * Observa o feed global de runs publicadas, ordenado da mais recente para a mais antiga.
     */
    fun getCommunityRuns(): Flow<List<Run>>
}
