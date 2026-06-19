/**
 * Reusable button components for DrivePulse.
 *
 * Camada: Core / Design System
 * Feature: Components
 */
package com.drivepulse.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary

/**
 * Primary action button with red background.
 *
 * @param text button label.
 * @param onClick callback when button is pressed.
 * @param modifier optional Modifier.
 * @param enabled whether the button is interactive.
 */
@Composable
fun DrivePulseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = DpPrimaryRed,
            contentColor = DpTextPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Secondary outlined button with red border.
 *
 * @param text button label.
 * @param onClick callback when button is pressed.
 * @param modifier optional Modifier.
 * @param enabled whether the button is interactive.
 */
@Composable
fun DrivePulseOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, DpPrimaryRed),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DpPrimaryRed
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
