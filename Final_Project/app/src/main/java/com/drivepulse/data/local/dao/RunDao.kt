/**
 * DAO (Data Access Object) para operações CRUD na tabela "runs".
 *
 * Camada: Data
 * Feature: Run
 *
 * Acedido exclusivamente através de [RunLocalDataSource] — nunca diretamente nos ViewModels.
 */
package com.drivepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.drivepulse.data.local.database.RunEntity
import com.drivepulse.data.local.database.RunStatisticsProjection
import kotlinx.coroutines.flow.Flow

/**
 * Operações de base de dados para [RunEntity].
 * Todas as queries retornam [Flow] para reatividade automática com Compose.
 */
@Dao
interface RunDao {

    /**
     * Insere uma nova run. Em caso de conflito de ID, substitui a existente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity)

    /**
     * Atualiza os campos de uma run existente (ex: ao finalizar).
     */
    @Update
    suspend fun updateRun(run: RunEntity)

    /**
     * Observa uma run pelo seu ID. Emite null se não existir.
     */
    @Query("SELECT * FROM runs WHERE id = :runId")
    fun getRunById(runId: String): Flow<RunEntity?>

    @Query(
        """
        SELECT * FROM runs
        WHERE userId = :userId
          AND endTime IS NOT NULL
          AND status != 'DISCARDED'
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    fun getRecentCompletedRuns(
        userId: String,
        limit: Int
    ): Flow<List<RunEntity>>

    @Query(
        """
        SELECT
            COUNT(*) AS totalRuns,
            COALESCE(SUM(distanceMeters), 0.0) AS totalDistanceMeters,
            COALESCE(SUM(durationSeconds), 0) AS totalDurationSeconds
        FROM runs
        WHERE userId = :userId
        """
    )
    fun getRunStatistics(userId: String): Flow<RunStatisticsProjection>

}
