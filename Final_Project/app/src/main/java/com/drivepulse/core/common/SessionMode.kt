/**
 * Session mode for guest vs authenticated access.
 *
 * Camada: Core / Common
 * Feature: Auth
 */
package com.drivepulse.core.common

/**
 * Represents the current user session mode.
 * Used throughout the app to gate features that require authentication.
 */
enum class SessionMode {
    /** User is browsing without an account. Limited actions available. */
    GUEST,
    /** User is logged in with a valid Firebase Auth session. */
    AUTHENTICATED
}

/**
 * CompositionLocal to provide the SessionMode throughout the app without prop drilling.
 */
val LocalSessionMode = androidx.compose.runtime.staticCompositionLocalOf { SessionMode.GUEST }
