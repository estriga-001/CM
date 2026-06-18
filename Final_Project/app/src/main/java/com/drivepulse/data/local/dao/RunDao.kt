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
import androidx.room.Delete
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

    /**
     * Observa todas as runs de um utilizador, ordenadas da mais recente para a mais antiga.
     */
    @Query("SELECT * FROM runs WHERE userId = :userId ORDER BY createdAt DESC")
    fun getRunsByUser(userId: String): Flow<List<RunEntity>>

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

    /**
     * Apaga a run. As coordenadas associadas são apagadas por CASCADE no [CoordinateEntity].
     */
    @Delete
    suspend fun deleteRun(run: RunEntity)

    /**
     * Apaga uma run diretamente pelo ID (alternativa ao @Delete para quando só temos o ID).
     */
    @Query("DELETE FROM runs WHERE id = :runId")
    suspend fun deleteRunById(runId: String)
}
