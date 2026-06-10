/**
 * Foreground Service para rastreio GPS contínuo de uma run de condução.
 *
 * Camada: Core / Location
 * Feature: Run
 *
 * Corre em foreground (com notificação visível) para evitar que o Android
 * mate o processo durante uma gravação longa. Usa FusedLocationTracker para
 * obter atualizações de GPS e expõe-as via SharedFlow para o ViewModel.
 *
 * Ciclo de vida:
 * 1. RunRecorderActivity chama startForegroundService() com o runId.
 * 2. onStartCommand() inicia o tracking e a notificação.
 * 3. onDestroy() para as coroutines e o GPS.
 */
package com.drivepulse.core.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.drivepulse.R
import com.drivepulse.core.common.Constants
import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.usecase.run.SaveCoordinateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground Service de rastreio GPS.
 *
 * Anotado com [@AndroidEntryPoint] para injeção Hilt.
 * Os UseCases e o LocationTracker são injetados automaticamente.
 */
@AndroidEntryPoint
class TrackingForegroundService : Service() {

    // -------------------------------------------------------------------------
    // Injeções Hilt
    // -------------------------------------------------------------------------

    @Inject
    lateinit var locationTracker: LocationTracker

    @Inject
    lateinit var saveCoordinateUseCase: SaveCoordinateUseCase

    // -------------------------------------------------------------------------
    // Estado do serviço
    // -------------------------------------------------------------------------

    /** Scope de coroutines ligado ao ciclo de vida do serviço. */
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** ID da run ativa — recebido via Intent Extra. */
    private var activeRunId: String? = null

    // -------------------------------------------------------------------------
    // Flow público — o ViewModel subscreve este Flow para atualizar o HUD
    // -------------------------------------------------------------------------

    companion object {
        /** Flow partilhado de localizações — exposto estaticamente para o ViewModel. */
        private val _locationFlow = MutableSharedFlow<Location>(replay = 1)
        val locationFlow: SharedFlow<Location> = _locationFlow.asSharedFlow()

        // Intent Actions
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"

        // Notification
        private const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        private const val NOTIFICATION_ID = 1001
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val runId = intent.getStringExtra(Constants.EXTRA_RUN_ID) ?: return START_NOT_STICKY
                activeRunId = runId
                Timber.d("🚗 TrackingService: START run=$runId")
                startForeground(NOTIFICATION_ID, createNotification())
                startLocationTracking(runId)
            }
            ACTION_PAUSE -> {
                Timber.d("⏸ TrackingService: PAUSE")
                // O tracking continua mas não guardamos coordenadas (implementado no ViewModel)
                updateNotification(getString(R.string.notification_tracking_paused))
            }
            ACTION_RESUME -> {
                Timber.d("▶ TrackingService: RESUME")
                updateNotification(getString(R.string.notification_tracking_recording))
            }
            ACTION_STOP -> {
                Timber.d("⏹ TrackingService: STOP")
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("🔴 TrackingService: onDestroy")
        serviceScope.cancel()
    }

    // -------------------------------------------------------------------------
    // Location Tracking
    // -------------------------------------------------------------------------

    /**
     * Inicia a coleta de localizações GPS e guarda cada ponto no Room.
     * Corre no [serviceScope] — cancela automaticamente quando o serviço morre.
     */
    private fun startLocationTracking(runId: String) {
        serviceScope.launch {
            locationTracker.getLocationUpdates().collect { location ->
                // Emite para o Flow estático (ViewModel subscreve para atualizar HUD)
                _locationFlow.emit(location)

                // Guarda a coordenada no Room via UseCase
                val coordinate = Coordinate(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude,
                    speed = location.speed,
                    timestamp = location.time
                )
                saveCoordinateUseCase(runId = runId, coordinate = coordinate)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Notification
    // -------------------------------------------------------------------------

    /**
     * Cria a notificação obrigatória para o Foreground Service.
     * Android 8+ requer um NotificationChannel criado antes de mostrar a notificação.
     */
    private fun createNotification(): Notification {
        createNotificationChannel()

        // Intent para abrir a app quando o utilizador toca na notificação
        val openAppIntent = packageManager
            .getLaunchIntentForPackage(packageName)
            ?.let { intent ->
                PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_tracking_title))
            .setContentText(getString(R.string.notification_tracking_recording))
            .setSmallIcon(R.drawable.ic_run_notification)
            .setOngoing(true) // Não pode ser dispensada pelo utilizador
            .setContentIntent(openAppIntent)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_tracking_title))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_run_notification)
            .setOngoing(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW // LOW = sem som, sem vibração
        ).apply {
            description = getString(R.string.notification_channel_desc)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
