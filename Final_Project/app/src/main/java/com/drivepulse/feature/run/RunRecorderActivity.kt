/**
 * Activity responsável pela gravação de percursos GPS.
 *
 * Camada: UI
 * Feature: Run
 *
 * Responsabilidades:
 * - Pedir permissões de localização em runtime (ACCESS_FINE_LOCATION).
 * - Lançar/parar o TrackingForegroundService.
 * - Integrar o RunRecorderViewModel via Hilt.
 * - Delegar toda a UI para RunRecorderScreen (Compose).
 * - Devolver resultado ao MainActivity via setResult().
 */
package com.drivepulse.feature.run

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepulse.core.common.Constants
import com.drivepulse.core.designsystem.theme.DrivePulseTheme
import com.drivepulse.core.location.TrackingForegroundService
import com.drivepulse.feature.run.screens.RunRecorderScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Activity de gravação de run com permissões e Foreground Service integrados.
 *
 * Fluxo:
 * 1. onCreate → verifica permissões.
 * 2. Se necessário → solicita ACCESS_FINE_LOCATION.
 * 3. Com permissão concedida → ViewModel cria run e lança o serviço.
 * 4. Ao terminar → serviço parado, resultado devolvido ao MainActivity.
 */
@AndroidEntryPoint
class RunRecorderActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val viewModel: RunRecorderViewModel by viewModels()

    // -------------------------------------------------------------------------
    // Permission launcher
    // -------------------------------------------------------------------------

    /**
     * Launcher para pedir ACCESS_FINE_LOCATION.
     * Quando o utilizador responde, notifica o ViewModel.
     */
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.d("✅ Permissão de localização concedida")
            val userId = firebaseAuth.currentUser?.uid ?: "guest"
            viewModel.onPermissionsGranted(userId = userId) { runId ->
                startTrackingService(runId)
            }
        } else {
            Timber.w("❌ Permissão de localização negada")
            viewModel.onPermissionDenied()
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrivePulseTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                RunRecorderScreen(
                    uiState = uiState,
                    onStartRun = { checkAndRequestPermissions() },
                    onPause = {
                        viewModel.onPauseRun()
                        sendServiceAction(TrackingForegroundService.ACTION_PAUSE)
                    },
                    onResume = {
                        viewModel.onResumeRun()
                        sendServiceAction(TrackingForegroundService.ACTION_RESUME)
                    },
                    onFinish = {
                        viewModel.onFinishRun()
                        stopTrackingService()
                        // Devolve resultado ao MainActivity (run gravada com sucesso)
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(
                                Constants.EXTRA_RUN_STATUS,
                                Constants.RUN_STATUS_DRAFT
                            )
                        })
                    },
                    onPublish = {
                        viewModel.onPublishRun()
                    },
                    onNavigateBack = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Garante que o serviço para se a Activity for fechada abruptamente
        stopTrackingService()
    }

    // -------------------------------------------------------------------------
    // Permissões
    // -------------------------------------------------------------------------

    /**
     * Verifica se a permissão já foi concedida antes de pedir ao utilizador.
     * Se já estiver concedida, inicia a run diretamente.
     */
    private fun checkAndRequestPermissions() {
        when {
            hasLocationPermission() -> {
                Timber.d("✅ Permissão já concedida — a iniciar run")
                val userId = firebaseAuth.currentUser?.uid ?: "guest"
                viewModel.onPermissionsGranted(userId = userId) { runId ->
                    startTrackingService(runId)
                }
            }
            else -> {
                Timber.d("📋 A pedir permissão de localização")
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // -------------------------------------------------------------------------
    // Foreground Service control
    // -------------------------------------------------------------------------

    /** Lança o TrackingForegroundService com o runId gerado pelo ViewModel. */
    private fun startTrackingService(runId: String) {
        val serviceIntent = Intent(this, TrackingForegroundService::class.java).apply {
            action = TrackingForegroundService.ACTION_START
            putExtra(Constants.EXTRA_RUN_ID, runId)
        }
        startForegroundService(serviceIntent)
        Timber.d("🚀 TrackingForegroundService iniciado para run=$runId")
    }

    /** Para o TrackingForegroundService. */
    private fun stopTrackingService() {
        val serviceIntent = Intent(this, TrackingForegroundService::class.java).apply {
            action = TrackingForegroundService.ACTION_STOP
        }
        startService(serviceIntent)
    }

    /** Envia uma action ao serviço em execução (PAUSE/RESUME). */
    private fun sendServiceAction(action: String) {
        val serviceIntent = Intent(this, TrackingForegroundService::class.java).apply {
            this.action = action
        }
        startService(serviceIntent)
    }
}
