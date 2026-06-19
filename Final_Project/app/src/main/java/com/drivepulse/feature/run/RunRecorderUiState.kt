/**
 * Estados da UI do ecrã de gravação de run (RunRecorderActivity).
 *
 * Camada: UI / Presentation
 * Feature: Run
 *
 * Segue o padrão MVI com estados fechados (sealed interface).
 * O ViewModel expõe estes estados via StateFlow.
 */
package com.drivepulse.feature.run

import com.drivepulse.domain.model.Coordinate

/**
 * Estado da UI da gravação de run.
 *
 * Transições de estado:
 * [Idle] -> [RequestingPermissions] -> [Tracking] <-> [Paused]
 *        \-> [PermissionDenied]
 * [Tracking] -> [Finished]
 * Qualquer -> [Error]
 */
sealed interface RunRecorderUiState {

    /**
     * Estado inicial — nenhuma run ativa, aguarda interação do utilizador.
     */
    data object Idle : RunRecorderUiState

    /**
     * A pedir permissões de localização em runtime.
     * Mostra um racional ao utilizador antes do diálogo do sistema.
     */
    data object RequestingPermissions : RunRecorderUiState

    /**
     * O utilizador recusou as permissões de localização.
     * Mostra botão para ir às definições da app.
     */
    data object PermissionDenied : RunRecorderUiState

    /**
     * Run ativa — a gravar coordenadas GPS em tempo real.
     *
     * @property runId ID da run ativa no Room.
     * @property durationSeconds segundos decorridos desde o início.
     * @property distanceMeters distância acumulada em metros.
     * @property currentSpeedKmh velocidade atual em km/h.
     * @property coordinates lista de pontos GPS para desenhar a polyline.
     * @property isPaused true se o utilizador pausou o cronómetro.
     */
    data class Tracking(
        val runId: String,
        val durationSeconds: Long = 0L,
        val distanceMeters: Float = 0f,
        val currentSpeedKmh: Float = 0f,
        val coordinates: List<Coordinate> = emptyList(),
        val isPaused: Boolean = false
    ) : RunRecorderUiState

    /**
     * Run concluída — mostra resumo antes de voltar ao MainActivity.
     *
     * @property runId ID da run guardada em Room (status DRAFT).
     * @property durationSeconds duração total.
     * @property distanceMeters distância total.
     * @property avgSpeedKmh velocidade média.
     */
    data class Finished(
        val runId: String,
        val durationSeconds: Long,
        val distanceMeters: Float,
        val avgSpeedKmh: Float
    ) : RunRecorderUiState

    /**
     * Erro inesperado (ex: serviço de localização indisponível).
     *
     * @property message mensagem de erro para mostrar ao utilizador.
     */
    data class Error(val message: String) : RunRecorderUiState
}
