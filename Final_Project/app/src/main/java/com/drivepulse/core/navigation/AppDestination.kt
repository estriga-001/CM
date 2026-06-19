/**
 * Navigation destination definitions for the main nav graph.
 *
 * Camada: Core / Navigation
 * Feature: Navigation
 */
package com.drivepulse.core.navigation

/**
 * All screen routes used within the main navigation graph.
 */
object AppDestination {
    const val HOME = "home"
    const val MAP = "map"
    const val COMMUNITY = "community"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val ABOUT = "about"
    const val CREATE_POST = "create_post/{runId}"
    const val PREMIUM = "premium"

    /** Build create_post route with a specific runId. */
    fun createPostRoute(runId: String) = "create_post/$runId"

}
