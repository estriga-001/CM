/**
 * Data source local para operações de runs via Room.
 *
 * Camada: Data
 * Feature: Run
 *
 * Abstrai os DAOs do Room da camada de repositório.
 * O RunRepositoryImpl usa este data source — nunca os DAOs diretamente.
 */
package com.drivepulse.data.local.datasource

import com.drivepulse.data.local.dao.CoordinateDao
import com.drivepulse.data.local.dao.RunDao
import com.drivepulse.data.local.database.CoordinateEntity
import com.drivepulse.data.local.database.RunEntity
import com.drivepulse.data.local.database.RunStatisticsProjection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Camada de acesso a dados local para runs e coordenadas GPS.
 *
 * Injetado como Singleton via Hilt. Delega diretamente para os DAOs
 * sem lógica adicional — o mapeamento e regras de negócio ficam no repositório/use cases.
 *
 * @param runDao DAO para a tabela "runs".
 * @param coordinateDao DAO para a tabela "run_coordinates".
 */
@Singleton
class RunLocalDataSource @Inject constructor(
    private val runDao: RunDao,
    private val coordinateDao: CoordinateDao
) {

    // -------------------------------------------------------------------------
    // Run operations
    // -------------------------------------------------------------------------

    /** Insere ou substitui uma run. */
    suspend fun insertRun(run: RunEntity) = runDao.insertRun(run)

    /** Atualiza uma run existente (ex: ao finalizar). */
    suspend fun updateRun(run: RunEntity) = runDao.updateRun(run)

    /** Observa uma run pelo ID. Emite null se não encontrada. */
    fun getRunById(runId: String): Flow<RunEntity?> = runDao.getRunById(runId)

    /** Observa todas as runs de um utilizador, ordenadas por data de criação. */
    fun getRunsByUser(userId: String): Flow<List<RunEntity>> = runDao.getRunsByUser(userId)

    /** Observa apenas os totais necessários para o resumo do perfil. */
    fun getRunStatistics(userId: String): Flow<RunStatisticsProjection> =
        runDao.getRunStatistics(userId)

    /** Apaga uma run pelo ID. As coordenadas são apagadas por CASCADE. */
    suspend fun deleteRunById(runId: String) = runDao.deleteRunById(runId)

    // -------------------------------------------------------------------------
    // Coordinate operations
    // -------------------------------------------------------------------------

    /** Insere um ponto GPS. */
    suspend fun insertCoordinate(coordinate: CoordinateEntity) =
        coordinateDao.insertCoordinate(coordinate)

    /** Observa todos os pontos GPS de uma run em tempo real. */
    fun getCoordinatesForRun(runId: String): Flow<List<CoordinateEntity>> =
        coordinateDao.getCoordinatesForRun(runId)

    /** Retorna o número de pontos capturados para uma run. */
    suspend fun getCoordinateCount(runId: String): Int =
        coordinateDao.getCoordinateCount(runId)
}
