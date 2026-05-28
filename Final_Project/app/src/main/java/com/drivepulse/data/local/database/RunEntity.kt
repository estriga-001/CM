/**
 * Room Entity para uma run gravada localmente.
 *
 * Camada: Data
 * Feature: Run
 *
 * Mapeado para a tabela "runs" na base de dados local Room.
 * Usa [RunMapper] para converter de/para [Run] de domínio.
 */
package com.drivepulse.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma linha na tabela [tableName] da base de dados Room.
 *
 * @property id UUID único da run (gerado localmente).
 * @property userId ID do utilizador Firebase dono da run.
 * @property title título/nome da rota.
 * @property startTime timestamp UNIX (ms) do início.
 * @property endTime timestamp UNIX (ms) do fim. Null enquanto em curso.
 * @property durationSeconds duração total calculada em segundos.
 * @property distanceMeters distância total percorrida em metros.
 * @property avgSpeedKmh velocidade média em km/h.
 * @property status estado: "DRAFT", "PUBLISHED" ou "DISCARDED".
 * @property createdAt timestamp de criação do registo (para ordenação).
 */
@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val startTime: Long,
    val endTime: Long?,
    val durationSeconds: Long,
    val distanceMeters: Float,
    val avgSpeedKmh: Float,
    val status: String,
    val createdAt: Long
)
