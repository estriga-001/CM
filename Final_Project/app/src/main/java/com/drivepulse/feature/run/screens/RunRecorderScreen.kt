/**
 * Ecrã principal de gravação de run (HUD).
 *
 * Camada: UI
 * Feature: Run
 *
 * Mostra um mapa com polyline em tempo real, cronómetro, distância,
 * velocidade e botões de controlo. Recebe o estado via [RunRecorderUiState].
 */
package com.drivepulse.feature.run.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpSurface
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.domain.model.Coordinate
import com.drivepulse.feature.run.RunRecorderUiState
import com.drivepulse.feature.run.screens.components.RunControlButtons
import com.drivepulse.feature.run.screens.components.RunStatsBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * HUD de gravação de run.
 *
 * @param uiState estado atual do ecrã (Idle, Tracking, Finished, Error, etc.).
 * @param onStartRun callback ao pressionar Start (depois das permissões).
 * @param onPause callback ao pausar.
 * @param onResume callback ao retomar.
 * @param onFinish callback ao terminar a run.
 * @param onNavigateBack callback para fechar a Activity (sem guardar).
 */
@Composable
fun RunRecorderScreen(
    uiState: RunRecorderUiState,
    onStartRun: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit,
    onPublish: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DpBackground)
    ) {
        when (uiState) {
            is RunRecorderUiState.Idle -> {
                IdleContent(onStartRun = onStartRun)
            }

            is RunRecorderUiState.RequestingPermissions -> {
                PermissionRationaleContent()
            }

            is RunRecorderUiState.PermissionDenied -> {
                PermissionDeniedContent(onNavigateBack = onNavigateBack)
            }

            is RunRecorderUiState.Tracking -> {
                TrackingContent(
                    state = uiState,
                    onPause = onPause,
                    onResume = onResume,
                    onFinish = onFinish
                )
            }

            is RunRecorderUiState.Finished -> {
                FinishedContent(
                    state = uiState,
                    onDone = onNavigateBack,
                    onPublish = onPublish
                )
            }

            is RunRecorderUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRetry = onStartRun,
                    onBack = onNavigateBack
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-ecrãs
// ---------------------------------------------------------------------------

@Composable
private fun IdleContent(onStartRun: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🏎️",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pronto para gravar",
            color = DpTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pressiona Start para iniciar o rastreio GPS",
            color = DpTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        RunControlButtons(
            isTracking = false,
            isPaused = false,
            onStart = onStartRun,
            onPause = {},
            onResume = {},
            onStop = {}
        )
    }
}

@Composable
private fun TrackingContent(
    state: RunRecorderUiState.Tracking,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(38.7169, -9.1399), // Lisboa como posição padrão inicial
            15f
        )
    }

    // Segue o último ponto GPS capturado com a câmara do mapa
    val lastCoord = state.coordinates.lastOrNull()
    LaunchedEffect(lastCoord) {
        lastCoord?.let { coord ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(coord.latitude, coord.longitude),
                    16f
                )
            )
        }
    }

    // Converte coordenadas de domínio para LatLng do Maps
    val polylinePoints = remember(state.coordinates) {
        state.coordinates.map { LatLng(it.latitude, it.longitude) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Mapa ocupa a maior parte do ecrã
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                // Polyline vermelha — desenha o percurso em tempo real
                if (polylinePoints.size >= 2) {
                    Polyline(
                        points = polylinePoints,
                        color = DpPrimaryRed,
                        width = 12f
                    )
                }
            }

            // Indicador de pausa sobreposto ao mapa
            if (state.isPaused) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "⏸ PAUSADO",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Barra de stats (distância | tempo | velocidade)
        RunStatsBar(
            durationSeconds = state.durationSeconds,
            distanceMeters = state.distanceMeters,
            currentSpeedKmh = state.currentSpeedKmh,
            modifier = Modifier.fillMaxWidth()
        )

        // Botões de controlo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DpSurface)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            RunControlButtons(
                isTracking = true,
                isPaused = state.isPaused,
                onStart = {},
                onPause = onPause,
                onResume = onResume,
                onStop = onFinish
            )
        }
    }
}

@Composable
private fun FinishedContent(
    state: RunRecorderUiState.Finished,
    onDone: () -> Unit,
    onPublish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "✅", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (state.isPublished) "Run Publicada!" else "Run Guardada Localmente",
            color = DpTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Resumo da run
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DpCard, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SummaryRow("Distância", "%.2f km".format(state.distanceMeters / 1000f))
                SummaryRow("Duração", formatDuration(state.durationSeconds))
                SummaryRow("Vel. Média", "%.1f km/h".format(state.avgSpeedKmh))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!state.isPublished) {
            Button(
                onClick = onPublish,
                colors = ButtonDefaults.buttonColors(containerColor = DpPrimaryRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar no Feed da Comunidade", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apenas Guardar", color = DpTextSecondary)
            }
        } else {
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = DpPrimaryRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar à Home", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = value, color = DpTextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = DpTextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun PermissionRationaleContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "📍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Permissão de Localização",
            color = DpTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "O DrivePulse precisa de acesso à tua localização para gravar o percurso.",
            color = DpTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PermissionDeniedContent(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🚫", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Acesso Negado",
            color = DpTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sem permissão de localização não é possível gravar runs. Ativa nas definições da app.",
            color = DpTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(containerColor = DpPrimaryRed)
        ) {
            Text("Voltar", color = Color.White)
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "⚠️", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Algo correu mal", color = DpTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = DpTextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = DpPrimaryRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tentar novamente", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar", color = DpTextSecondary)
        }
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
