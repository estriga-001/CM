/**
 * Theme composable for DrivePulse.
 * Wraps Material 3 with the DrivePulse dark color scheme, typography and shapes.
 *
 * Camada: Core / Design System
 * Feature: Theme
 */
package com.drivepulse.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * DrivePulse dark color scheme mapping project colors to Material 3 roles.
 */
private val DrivePulseDarkColorScheme = darkColorScheme(
    primary = DpPrimaryRed,
    onPrimary = DpTextPrimary,
    primaryContainer = DpPrimaryRedDark,
    onPrimaryContainer = DpTextPrimary,
    secondary = DpPrimaryRedSoft,
    onSecondary = DpTextPrimary,
    secondaryContainer = DpCardElevated,
    onSecondaryContainer = DpTextPrimary,
    tertiary = DpInfo,
    onTertiary = DpTextPrimary,
    background = DpBackground,
    onBackground = DpTextPrimary,
    surface = DpSurface,
    onSurface = DpTextPrimary,
    surfaceVariant = DpSurfaceVariant,
    onSurfaceVariant = DpTextSecondary,
    error = DpDanger,
    onError = DpTextPrimary,
    outline = DpDivider,
    outlineVariant = DpDivider
)

/**
 * Root theme composable for the entire DrivePulse app.
 * Always uses dark mode as per the project's visual identity.
 *
 * @param content The composable content to theme.
 */
@Composable
fun DrivePulseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DrivePulseDarkColorScheme,
        typography = DrivePulseTypography,
        shapes = DrivePulseShapes,
        content = content
    )
}
