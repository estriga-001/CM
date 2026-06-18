/**
 * Interface do repositório para operações de Run.
 *
 * Camada: Domain
 * Feature: Run
 *
 * Regra da Clean Architecture: esta interface não conhece nenhum detalhe de implementação
 * (Room, Firestore, etc.). Os ViewModels e UseCases dependem apenas deste contrato.
 */
package com.drivepulse.domain.repository

import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.model.Run
import com.drivepulse.domain.model.RunStatistics
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a dados para runs de condução.
 * Implementado na camada Data por [RunRepositoryImpl].
 */
interface RunRepository {

    /**
     * Cria uma nova run em modo DRAFT na base de dados local.
     *
     * @param userId ID do utilizador autenticado.
     * @param title título inicial da run (pode ser editado posteriormente).
     * @return o [runId] (UUID) da run recém-criada.
     */
    suspend fun createRun(userId: String, title: String = ""): String

    /**
     * Adiciona um ponto GPS à run em curso.
     *
     * @param runId ID da run ativa.
     * @param coordinate ponto de localização capturado.
     */
    suspend fun addCoordinate(runId: String, coordinate: Coordinate)

    /**
     * Finaliza uma run, registando duração, distância total e velocidade média.
     * A run mantém-se em estado DRAFT até ser publicada.
     *
     * @param runId ID da run a finalizar.
     * @param durationSeconds duração total em segundos.
     * @param distanceMeters distância total em metros.
     * @param avgSpeedKmh velocidade média em km/h.
     */
    suspend fun finishRun(
        runId: String,
        durationSeconds: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float
    )

    /**
     * Observa uma run específica (com as suas coordenadas) via Flow.
     * Emite sempre que a run ou as suas coordenadas mudarem.
     */
    fun getRunById(runId: String): Flow<Run?>

    /**
     * Observa a lista de todas as runs de um utilizador, ordenadas por data de criação.
     */
    fun getRunsByUser(userId: String): Flow<List<Run>>

    /**
     * Observa os totais agregados das runs sem carregar todas as linhas em memória.
     */
    fun getRunStatistics(userId: String): Flow<RunStatistics>

    /**
     * Apaga uma run e todas as suas coordenadas associadas.
     */
    suspend fun deleteRun(runId: String)

    /**
     * Publica uma run local no feed da comunidade (cloud).
     * @param runId O ID local da run a publicar.
     * @param userName O nome de exibição do utilizador.
     */
    suspend fun publishRun(runId: String, userName: String)

    /**
     * Observa a lista global de runs publicadas pela comunidade.
     */
    fun getCommunityRuns(): Flow<List<Run>>
}
