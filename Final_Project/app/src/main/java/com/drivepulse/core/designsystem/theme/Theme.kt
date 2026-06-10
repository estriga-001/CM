/**
 * Theme composable for DrivePulse.
 * Wraps Material 3 with the DrivePulse color scheme, typography and shapes.
 *
 * Camada: Core / Design System
 * Feature: Theme
 */
package com.drivepulse.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * DrivePulse dark color scheme — main identity of the app.
 */
private val DrivePulseDarkColorScheme = darkColorScheme(
    primary = DpPrimaryRed,
    onPrimary = DpTextPrimary_Static,
    primaryContainer = DpPrimaryRedDark,
    onPrimaryContainer = DpTextPrimary_Static,
    secondary = DpPrimaryRedSoft,
    onSecondary = DpTextPrimary_Static,
    secondaryContainer = DpCardElevated_Static,
    onSecondaryContainer = DpTextPrimary_Static,
    tertiary = DpInfo,
    onTertiary = DpTextPrimary_Static,
    background = DpBackground_Static,
    onBackground = DpTextPrimary_Static,
    surface = DpSurface_Static,
    onSurface = DpTextPrimary_Static,
    surfaceVariant = DpSurfaceVariant_Static,
    onSurfaceVariant = DpTextSecondary_Static,
    error = DpDanger,
    onError = DpTextPrimary_Static,
    outline = DpDivider,
    outlineVariant = DpDivider
)

/**
 * DrivePulse light color scheme — clean, contrast-preserving alternative.
 */
private val DrivePulseLightColorScheme = lightColorScheme(
    primary = DpPrimaryRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = DpPrimaryRedSoft,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDEA),
    onSecondaryContainer = Color(0xFF2C0001),
    tertiary = DpInfo,
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF2F2F5),
    onSurfaceVariant = Color(0xFF49454F),
    error = DpDanger,
    onError = Color.White,
    outline = Color(0xFFDDDDE5),
    outlineVariant = Color(0xFFE9E9F0)
)

/**
 * Root theme composable for the entire DrivePulse app.
 *
 * @param darkTheme Whether to use dark mode. Defaults to following the system setting.
 * @param content The composable content to theme.
 */
@Composable
fun DrivePulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DrivePulseDarkColorScheme else DrivePulseLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DrivePulseTypography,
        shapes = DrivePulseShapes,
        content = content
    )
}
