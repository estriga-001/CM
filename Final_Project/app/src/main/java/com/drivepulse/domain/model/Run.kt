/**
 * Domain model representing a recorded driving run.
 *
 * Camada: Domain
 * Feature: Run
 *
 * Contém apenas lógica de domínio pura — sem imports Android ou Firebase.
 */
package com.drivepulse.domain.model

/**
 * Representa uma viagem/percurso registado pelo condutor.
 *
 * @property id identificador único (UUID gerado localmente).
 * @property userId ID do utilizador dono desta run.
 * @property title título/nome da run (pode ser editado antes de publicar).
 * @property startTime timestamp UNIX em milissegundos do início da gravação.
 * @property endTime timestamp UNIX em milissegundos do fim. Null enquanto em curso.
 * @property durationSeconds duração total em segundos.
 * @property distanceMeters distância total percorrida em metros.
 * @property avgSpeedKmh velocidade média em km/h.
 * @property status estado atual da run (DRAFT, PUBLISHED ou DISCARDED).
 * @property coordinates lista de pontos GPS ordenados por timestamp.
 */
data class Run(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val startTime: Long = 0L,
    val endTime: Long? = null,
    val durationSeconds: Long = 0L,
    val distanceMeters: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val status: RunStatus = RunStatus.DRAFT,
    val coordinates: List<Coordinate> = emptyList()
)

/**
 * Estado do ciclo de vida de uma run.
 *
 * - [DRAFT]: gravada localmente, ainda não publicada.
 * - [PUBLISHED]: partilhada na comunidade via Firestore.
 * - [DISCARDED]: descartada pelo utilizador, pode ser apagada.
 */
enum class RunStatus {
    DRAFT,
    PUBLISHED,
    DISCARDED
}
