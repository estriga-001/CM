/**
 * Domain model representing a single GPS coordinate captured during a run.
 *
 * Camada: Domain
 * Feature: Run
 *
 * Sem imports Android — modelo puro de domínio.
 */
package com.drivepulse.domain.model

/**
 * Um ponto de localização GPS capturado durante uma run.
 *
 * @property latitude latitude em graus decimais (WGS84).
 * @property longitude longitude em graus decimais (WGS84).
 * @property altitude altitude em metros acima do nível do mar.
 * @property speed velocidade instantânea em m/s no momento da captura.
 * @property timestamp timestamp UNIX em milissegundos da captura.
 */
data class Coordinate(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val speed: Float = 0f,
    val timestamp: Long = 0L
)
