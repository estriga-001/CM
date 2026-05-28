/**
 * Reusable card components for DrivePulse.
 *
 * Camada: Core / Design System
 * Feature: Components
 */
package com.drivepulse.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpCardElevated
import com.drivepulse.core.designsystem.theme.Spacing

/**
 * Standard card with DrivePulse dark surface color.
 *
 * @param modifier optional Modifier.
 * @param content the card content.
 */
@Composable
fun DrivePulseCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = DpCard
        ),
        content = content
    )
}

/**
 * Elevated card with slightly lighter background for visual hierarchy.
 *
 * @param modifier optional Modifier.
 * @param onClick optional click handler.
 * @param content the card content.
 */
@Composable
fun DrivePulseElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = DpCardElevated
            ),
            content = content
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = DpCardElevated
            ),
            content = content
        )
    }
}
