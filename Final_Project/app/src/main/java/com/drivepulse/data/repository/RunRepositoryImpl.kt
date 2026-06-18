/**
 * Implementação do RunRepository para a camada de dados local (Room).
 *
 * Camada: Data
 * Feature: Run
 *
 * Mapeia entre entidades Room ([RunEntity], [CoordinateEntity]) e
 * modelos de domínio ([Run], [Coordinate]) usando funções de extensão locais.
 * Usa [RunLocalDataSource] — nunca acede aos DAOs diretamente.
 */
package com.drivepulse.data.repository

import com.drivepulse.data.local.database.CoordinateEntity
import com.drivepulse.data.local.database.RunEntity
import com.drivepulse.data.local.datasource.RunLocalDataSource
import com.drivepulse.data.remote.datasource.RunRemoteDataSource
import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.model.Run
import com.drivepulse.domain.model.RunStatistics
import com.drivepulse.domain.model.RunStatus
import com.drivepulse.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do [RunRepository] para persistência local via Room.
 *
 * @param localDataSource fonte de dados local (Room).
 */
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val localDataSource: RunLocalDataSource,
    private val remoteDataSource: RunRemoteDataSource
) : RunRepository {

    // -------------------------------------------------------------------------
    // Write operations
    // -------------------------------------------------------------------------

    override suspend fun createRun(userId: String, title: String): String {
        val runId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val entity = RunEntity(
            id = runId,
            userId = userId,
            title = title.ifBlank { "Run $now" },
            startTime = now,
            endTime = null,
            durationSeconds = 0L,
            distanceMeters = 0f,
            avgSpeedKmh = 0f,
            status = RunStatus.DRAFT.name,
            createdAt = now
        )
        localDataSource.insertRun(entity)
        return runId
    }

    override suspend fun addCoordinate(runId: String, coordinate: Coordinate) {
        localDataSource.insertCoordinate(coordinate.toEntity(runId))
    }

    override suspend fun finishRun(
        runId: String,
        durationSeconds: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float
    ) {
        // first() lê o valor atual do Flow uma única vez (não bloqueia indefinidamente)
        val entity = localDataSource.getRunById(runId).first() ?: return
        val updated = entity.copy(
            endTime = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            distanceMeters = distanceMeters,
            avgSpeedKmh = avgSpeedKmh
        )
        localDataSource.updateRun(updated)
    }

    override suspend fun deleteRun(runId: String) {
        localDataSource.deleteRunById(runId)
    }

    // -------------------------------------------------------------------------
    // Read operations (reactive Flow)
    // -------------------------------------------------------------------------

    override fun getRunById(runId: String): Flow<Run?> {
        // Combina o Flow da run com o Flow das coordenadas para sempre ter o estado completo
        return combine(
            localDataSource.getRunById(runId),
            localDataSource.getCoordinatesForRun(runId)
        ) { runEntity, coordinates ->
            runEntity?.toDomain(coordinates)
        }
    }

    override fun getRunsByUser(userId: String): Flow<List<Run>> {
        // Lista sem coordenadas — para o feed só precisamos das stats
        return localDataSource.getRunsByUser(userId).map { entities ->
            entities.map { it.toDomain(emptyList()) }
        }
    }

    override fun getRunStatistics(userId: String): Flow<RunStatistics> {
        return localDataSource.getRunStatistics(userId).map { projection ->
            RunStatistics(
                totalRuns = projection.totalRuns,
                totalDistanceMeters = projection.totalDistanceMeters,
                totalDurationSeconds = projection.totalDurationSeconds
            )
        }
    }

    // -------------------------------------------------------------------------
    // Remote operations (Firestore)
    // -------------------------------------------------------------------------

    override suspend fun publishRun(runId: String, userName: String) {
        // 1. Obter a Run local completa (com coordenadas)
        val runEntity = localDataSource.getRunById(runId).first() ?: return
        val coordinates = localDataSource.getCoordinatesForRun(runId).first()
        val run = runEntity.toDomain(coordinates)

        // 2. Publicar na cloud
        remoteDataSource.publishRun(run, userName)

        // 3. Atualizar o estado local para PUBLISHED
        localDataSource.updateRun(runEntity.copy(status = RunStatus.PUBLISHED.name))
    }

    override fun getCommunityRuns(): Flow<List<Run>> {
        return remoteDataSource.getCommunityRuns()
    }

    // -------------------------------------------------------------------------
    // Mappers (privados — não devem sair desta classe)
    // -------------------------------------------------------------------------

    private fun RunEntity.toDomain(coordinates: List<CoordinateEntity>): Run = Run(
        id = id,
        userId = userId,
        title = title,
        startTime = startTime,
        endTime = endTime,
        durationSeconds = durationSeconds,
        distanceMeters = distanceMeters,
        avgSpeedKmh = avgSpeedKmh,
        status = RunStatus.valueOf(status),
        coordinates = coordinates.map { it.toDomain() }
    )

    private fun CoordinateEntity.toDomain(): Coordinate = Coordinate(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        timestamp = timestamp
    )

    private fun Coordinate.toEntity(runId: String): CoordinateEntity = CoordinateEntity(
        runId = runId,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        timestamp = timestamp
    )
}
