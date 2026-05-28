/**
 * Shape tokens for the DrivePulse design system.
 * Defines rounded corner shapes for cards, buttons, inputs, etc.
 *
 * Camada: Core / Design System
 * Feature: Theme
 */
package com.drivepulse.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 shape scheme with consistent rounded corners.
 */
val DrivePulseShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)
