/**
 * Color tokens for the DrivePulse design system.
 * Dark-first palette with red as the primary accent.
 *
 * Camada: Core / Design System
 * Feature: Theme
 */
package com.drivepulse.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

// --- Static Colors (internal to theme definitions) ---
val DpBackground_Static = Color(0xFF09090B)
val DpSurface_Static = Color(0xFF121216)
val DpSurfaceVariant_Static = Color(0xFF1A1A20)
val DpCard_Static = Color(0xFF121216)
val DpCardElevated_Static = Color(0xFF1A1A20)
val DpTextPrimary_Static = Color(0xFFF5F5F5)
val DpTextSecondary_Static = Color(0xFFA1A1AA)
val DpTextMuted_Static = Color(0xFF737373)

// --- Dynamic Colors (resolved using current theme context) ---
val DpBackground: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val DpSurface: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val DpSurfaceVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val DpCard: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val DpCardElevated: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val DpTextPrimary: Color
    @Composable
    get() = MaterialTheme.colorScheme.onBackground

val DpTextSecondary: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val DpTextMuted: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

// --- Primary Red ---
val DpPrimaryRed = Color(0xFFE50914)
val DpPrimaryRedDark = Color(0xFF8B0000)
val DpPrimaryRedSoft = Color(0xFFFF3B3B)

// --- Semantic ---
val DpSuccess = Color(0xFF22C55E)
val DpWarning = Color(0xFFF59E0B)
val DpDanger = Color(0xFFEF4444)
val DpInfo = Color(0xFF38BDF8)

// --- Divider ---
val DpDivider = Color(0xFF2A2A32)

// --- Map Pin Colors ---
val DpPinView = Color(0xFFF97316)
val DpPinRoad = Color(0xFFE50914)
val DpPinCurves = Color(0xFFA855F7)
val DpPinCoast = Color(0xFF38BDF8)
val DpPinMountain = Color(0xFF84CC16)
val DpPinNight = Color(0xFF6366F1)
val DpPinPhoto = Color(0xFFF472B6)
val DpPinCafe = Color(0xFFFBBF24)
val DpPinMeeting = Color(0xFF14B8A6)
val DpPinEvent = Color(0xFFE50914)
val DpPinWarning = Color(0xFFEF4444)
