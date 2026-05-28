/**
 * ViewModel para o ecrã de gravação de run (RunRecorderActivity).
 *
 * Camada: Presentation
 * Feature: Run
 *
 * Responsabilidades:
 * - Verificar estado das permissões de localização.
 * - Controlar o ciclo de vida da run (Start/Pause/Resume/Stop).
 * - Calcular métricas em tempo real (tempo, distância, velocidade).
 * - Expor [RunRecorderUiState] via StateFlow para a UI.
 *
 * NÃO faz queries diretamente ao Room nem acede ao FirebaseAuth.
 * Usa UseCases para toda a lógica de negócio.
 */
package com.drivepulse.feature.run

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.location.TrackingForegroundService
import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.usecase.run.FinishRunUseCase
import com.drivepulse.domain.usecase.run.PublishRunUseCase
import com.drivepulse.domain.usecase.run.StartRunUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel da gravação de runs.
 *
 * @param startRunUseCase cria uma nova run DRAFT no Room.
 * @param finishRunUseCase finaliza a run com as métricas calculadas.
 */
@HiltViewModel
class RunRecorderViewModel @Inject constructor(
    private val startRunUseCase: StartRunUseCase,
    private val finishRunUseCase: FinishRunUseCase,
    private val publishRunUseCase: PublishRunUseCase
) : ViewModel() {

    // -------------------------------------------------------------------------
    // UI State
    // -------------------------------------------------------------------------

    private val _uiState = MutableStateFlow<RunRecorderUiState>(RunRecorderUiState.Idle)
    val uiState: StateFlow<RunRecorderUiState> = _uiState.asStateFlow()

    // -------------------------------------------------------------------------
    // Tracking state (interno)
    // -------------------------------------------------------------------------

    /** Job do cronómetro — cancelado ao pausar. */
    private var timerJob: Job? = null

    /** Job de coleta do flow de localização do serviço. */
    private var locationJob: Job? = null

    /** Timestamp de início da run atual (para calcular duração). */
    private var startTimeMs: Long = 0L

    /** Segundos acumulados ao pausar (para retomar sem perder tempo). */
    private var accumulatedSeconds: Long = 0L

    /** Última localização recebida (para calcular distância incremental). */
    private var lastLocation: Location? = null

    /** Distância total acumulada em metros. */
    private var totalDistanceMeters: Float = 0f

    // -------------------------------------------------------------------------
    // Eventos da UI
    // -------------------------------------------------------------------------

    /**
     * Chamado quando as permissões de localização foram concedidas.
     * Cria a run no Room e inicia o tracking.
     *
     * @param userId ID do utilizador autenticado (ou "guest").
     * @param onStartService callback para lançar o TrackingForegroundService.
     */
    fun onPermissionsGranted(userId: String, onStartService: (runId: String) -> Unit) {
        viewModelScope.launch {
            try {
                val runId = startRunUseCase(userId = userId)
                Timber.d("✅ Run criada: runId=$runId")
                startTimeMs = System.currentTimeMillis()
                totalDistanceMeters = 0f
                lastLocation = null
                accumulatedSeconds = 0L

                _uiState.update {
                    RunRecorderUiState.Tracking(runId = runId)
                }

                onStartService(runId)
                startTimer()
                startCollectingLocations()
            } catch (e: Exception) {
                Timber.e(e, "❌ Erro ao criar run")
                _uiState.update { RunRecorderUiState.Error("Não foi possível iniciar a run.") }
            }
        }
    }

    /** Chamado quando o utilizador pausa a gravação. */
    fun onPauseRun() {
        val currentState = _uiState.value as? RunRecorderUiState.Tracking ?: return
        timerJob?.cancel()
        accumulatedSeconds = currentState.durationSeconds
        _uiState.update { currentState.copy(isPaused = true) }
        Timber.d("⏸ Run pausada aos ${currentState.durationSeconds}s")
    }

    /** Chamado quando o utilizador retoma a gravação. */
    fun onResumeRun() {
        val currentState = _uiState.value as? RunRecorderUiState.Tracking ?: return
        startTimeMs = System.currentTimeMillis()
        _uiState.update { currentState.copy(isPaused = false) }
        startTimer()
        Timber.d("▶ Run retomada")
    }

    /**
     * Chamado quando o utilizador termina a run.
     * Persiste as métricas finais via FinishRunUseCase.
     */
    fun onFinishRun() {
        val currentState = _uiState.value as? RunRecorderUiState.Tracking ?: return
        timerJob?.cancel()
        locationJob?.cancel()

        viewModelScope.launch {
            try {
                finishRunUseCase(
                    runId = currentState.runId,
                    durationSeconds = currentState.durationSeconds,
                    distanceMeters = totalDistanceMeters
                )

                val avgSpeedKmh = if (currentState.durationSeconds > 0) {
                    (totalDistanceMeters / 1000f) / (currentState.durationSeconds / 3600f)
                } else 0f

                _uiState.update {
                    RunRecorderUiState.Finished(
                        runId = currentState.runId,
                        durationSeconds = currentState.durationSeconds,
                        distanceMeters = totalDistanceMeters,
                        avgSpeedKmh = avgSpeedKmh
                    )
                }
                Timber.d("⏹ Run finalizada: ${totalDistanceMeters}m em ${currentState.durationSeconds}s")
            } catch (e: Exception) {
                Timber.e(e, "❌ Erro ao finalizar run")
                _uiState.update { RunRecorderUiState.Error("Erro ao guardar a run.") }
            }
        }
    }

    /**
     * Publica a rota finalizada no feed comunitário.
     */
    fun onPublishRun() {
        val currentState = _uiState.value as? RunRecorderUiState.Finished ?: return
        
        // Coloca num estado temporário de loading se necessário, para este protótipo vamos
        // usar um estado simples ou apenas invocar e navegar no sucesso.
        viewModelScope.launch {
            try {
                publishRunUseCase(currentState.runId)
                // Se sucesso, atualizar a UI para um estado de finalizado com sucesso
                _uiState.update { currentState.copy(isPublished = true) }
            } catch (e: Exception) {
                Timber.e(e, "❌ Erro ao publicar rota")
                _uiState.update { RunRecorderUiState.Error("Não foi possível publicar a rota. Verifica a internet.") }
            }
        }
    }

    /** Chamado quando o utilizador nega permissões. */
    fun onPermissionDenied() {
        _uiState.update { RunRecorderUiState.PermissionDenied }
    }

    // -------------------------------------------------------------------------
    // Timer (cronómetro interno)
    // -------------------------------------------------------------------------

    /**
     * Lança uma coroutine que incrementa o contador de segundos a cada segundo.
     * Cancela automaticamente ao pausar ou parar.
     */
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            startTimeMs = System.currentTimeMillis()
            while (true) {
                delay(1_000L)
                val elapsedSinceResume = (System.currentTimeMillis() - startTimeMs) / 1000L
                val totalSeconds = accumulatedSeconds + elapsedSinceResume

                _uiState.update { state ->
                    (state as? RunRecorderUiState.Tracking)?.copy(
                        durationSeconds = totalSeconds
                    ) ?: state
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Location collection
    // -------------------------------------------------------------------------

    /**
     * Subscreve o SharedFlow do TrackingForegroundService para calcular
     * distância e velocidade em tempo real e atualizar a polyline no mapa.
     */
    private fun startCollectingLocations() {
        locationJob = viewModelScope.launch {
            TrackingForegroundService.locationFlow.collect { location ->
                val currentState = _uiState.value as? RunRecorderUiState.Tracking ?: return@collect
                if (currentState.isPaused) return@collect

                // Calcula distância incremental em metros usando a fórmula de Haversine
                val distanceIncrement = lastLocation?.distanceTo(location) ?: 0f
                totalDistanceMeters += distanceIncrement
                lastLocation = location

                val speedKmh = location.speed * 3.6f  // m/s -> km/h

                val newCoordinate = Coordinate(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude,
                    speed = location.speed,
                    timestamp = location.time
                )

                _uiState.update {
                    currentState.copy(
                        distanceMeters = totalDistanceMeters,
                        currentSpeedKmh = speedKmh,
                        coordinates = currentState.coordinates + newCoordinate
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        locationJob?.cancel()
    }
}
