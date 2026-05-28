/**
 * Botão de controlo da run (Start / Pause / Resume / Stop).
 *
 * Camada: UI
 * Feature: Run
 *
 * Botão circular grande com ícone e cor adaptados ao estado atual.
 * Inclui animação de pulso quando a run está ativa.
 */
package com.drivepulse.feature.run.screens.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpSurfaceVariant

/**
 * Conjunto de botões de controlo de gravação.
 *
 * Estados possíveis:
 * - [isTracking]=false, [isPaused]=false → Botão Start (vermelho)
 * - [isTracking]=true,  [isPaused]=false → Botão Pause + Botão Stop
 * - [isTracking]=true,  [isPaused]=true  → Botão Resume + Botão Stop
 *
 * @param isTracking true se existe uma run ativa.
 * @param isPaused true se a run está pausada.
 * @param onStart callback ao iniciar.
 * @param onPause callback ao pausar.
 * @param onResume callback ao retomar.
 * @param onStop callback ao parar e guardar.
 */
@Composable
fun RunControlButtons(
    isTracking: Boolean,
    isPaused: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(bottom = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isTracking) {
            // Estado: Idle — botão Start com animação de pulso
            PulsingButton(
                onClick = onStart,
                color = DpPrimaryRed,
                size = 80
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Iniciar Run",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        } else {
            // Estado: Tracking — botão Pause/Resume + botão Stop
            if (isPaused) {
                // Resume
                PulsingButton(
                    onClick = onResume,
                    color = DpPrimaryRed,
                    size = 72,
                    animate = false
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Retomar Run",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                // Pause
                PulsingButton(
                    onClick = onPause,
                    color = Color(0xFFE5A009),
                    size = 72,
                    animate = true
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pausar Run",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Botão Stop — sempre visível quando a run está ativa
            PulsingButton(
                onClick = onStop,
                color = DpSurfaceVariant,
                size = 64,
                animate = false
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Terminar Run",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

/**
 * Botão circular com animação de pulso opcional.
 */
@Composable
private fun PulsingButton(
    onClick: () -> Unit,
    color: Color,
    size: Int,
    animate: Boolean = false,
    content: @Composable () -> Unit
) {
    val scale = if (animate) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val animatedScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
        animatedScale
    } else {
        1f
    }

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    ) {
        content()
    }
}
