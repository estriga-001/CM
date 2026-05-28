/**
 * Barra de estatísticas em tempo real durante uma run.
 *
 * Camada: UI
 * Feature: Run
 *
 * Mostra: Distância (km) | Tempo (HH:MM:SS) | Velocidade (km/h)
 */
package com.drivepulse.feature.run.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.core.designsystem.theme.DpSurface
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary

/**
 * Barra horizontal com 3 métricas principais da run.
 *
 * @param durationSeconds segundos decorridos (convertidos para HH:MM:SS internamente).
 * @param distanceMeters distância acumulada em metros (convertida para km).
 * @param currentSpeedKmh velocidade instantânea em km/h.
 * @param modifier modificador opcional.
 */
@Composable
fun RunStatsBar(
    durationSeconds: Long,
    distanceMeters: Float,
    currentSpeedKmh: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DpSurface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(
            label = "DISTÂNCIA",
            value = "%.2f km".format(distanceMeters / 1000f)
        )
        StatDivider()
        StatItem(
            label = "TEMPO",
            value = formatDuration(durationSeconds)
        )
        StatDivider()
        StatItem(
            label = "VELOCIDADE",
            value = "%.1f km/h".format(currentSpeedKmh)
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = DpTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            color = DpTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.2.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatDivider() {
    Text(
        text = "|",
        color = DpTextSecondary,
        fontSize = 20.sp
    )
}

/** Converte segundos para o formato HH:MM:SS. */
private fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
