/**
 * DTO (Data Transfer Object) para representar uma Run no Firebase Firestore.
 *
 * Camada: Data (Remote)
 * Feature: Community / Sync
 */
package com.drivepulse.data.remote.model

import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.model.Run
import com.drivepulse.domain.model.RunStatus

/**
 * Representação Firestore de uma [Run].
 * Todos os parâmetros têm valores padrão para permitir a desserialização automática pelo SDK do Firestore.
 */
data class RunDto(
    val id: String = "",
    val userId: String = "",
    val userName: String = "Condutor", // Nome de exibição para o feed
    val title: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val durationSeconds: Long = 0L,
    val distanceMeters: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val status: String = RunStatus.PUBLISHED.name,
    val coordinates: List<CoordinateDto> = emptyList()
) {
    /** Converte este DTO num modelo de domínio puro. */
    fun toDomain(): Run = Run(
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
}

/**
 * Representação Firestore de uma [Coordinate].
 */
data class CoordinateDto(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val speed: Float = 0f,
    val timestamp: Long = 0L
) {
    fun toDomain(): Coordinate = Coordinate(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        timestamp = timestamp
    )
}

/**
 * Extensões para converter o modelo de Domínio para DTO de Firestore.
 */
fun Run.toDto(userName: String = "Condutor"): RunDto = RunDto(
    id = id,
    userId = userId,
    userName = userName,
    title = title,
    startTime = startTime,
    endTime = endTime ?: 0L,
    durationSeconds = durationSeconds,
    distanceMeters = distanceMeters,
    avgSpeedKmh = avgSpeedKmh,
    status = RunStatus.PUBLISHED.name, // Força o status publicado ao enviar para a cloud
    coordinates = coordinates.map { it.toDto() }
)

fun Coordinate.toDto(): CoordinateDto = CoordinateDto(
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    speed = speed,
    timestamp = timestamp
)
