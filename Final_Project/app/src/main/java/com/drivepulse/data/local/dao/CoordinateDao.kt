/**
 * DAO (Data Access Object) para operações na tabela "run_coordinates".
 *
 * Camada: Data
 * Feature: Run
 *
 * Acedido exclusivamente através de [RunLocalDataSource].
 */
package com.drivepulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drivepulse.data.local.database.CoordinateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Operações de base de dados para [CoordinateEntity].
 */
@Dao
interface CoordinateDao {

    /**
     * Insere um ponto GPS. Ignora conflitos (caso raro de IDs autogerados colidirem).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCoordinate(coordinate: CoordinateEntity)

    /**
     * Observa todos os pontos GPS de uma run, ordenados cronologicamente.
     * Atualiza automaticamente em tempo real quando novos pontos são inseridos.
     */
    @Query("SELECT * FROM run_coordinates WHERE runId = :runId ORDER BY timestamp ASC")
    fun getCoordinatesForRun(runId: String): Flow<List<CoordinateEntity>>

    /**
     * Retorna o número total de pontos capturados para uma run.
     * Útil para calcular qualidade do track antes de publicar.
     */
    @Query("SELECT COUNT(*) FROM run_coordinates WHERE runId = :runId")
    suspend fun getCoordinateCount(runId: String): Int
}
