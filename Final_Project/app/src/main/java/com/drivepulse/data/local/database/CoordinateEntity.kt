/**
 * Room Entity para um ponto de coordenada GPS de uma run.
 *
 * Camada: Data
 * Feature: Run
 *
 * Mapeado para a tabela "run_coordinates". Tem uma relação N:1 com [RunEntity].
 */
package com.drivepulse.data.local.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa um ponto GPS individual capturado durante uma run.
 *
 * @property id auto-gerado pelo Room (Long autoincrement).
 * @property runId FK para a [RunEntity] pai. Index para queries eficientes.
 * @property latitude latitude em graus decimais (WGS84).
 * @property longitude longitude em graus decimais (WGS84).
 * @property altitude altitude em metros.
 * @property speed velocidade instantânea em m/s.
 * @property timestamp timestamp UNIX (ms) da captura.
 */
@Entity(
    tableName = "run_coordinates",
    foreignKeys = [
        ForeignKey(
            entity = RunEntity::class,
            parentColumns = ["id"],
            childColumns = ["runId"],
            onDelete = ForeignKey.CASCADE  // Apaga coordenadas quando a run é apagada
        )
    ],
    indices = [Index(value = ["runId"])]
)
data class CoordinateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val runId: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val timestamp: Long
)
