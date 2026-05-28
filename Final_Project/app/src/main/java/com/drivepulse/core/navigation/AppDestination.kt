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
    const val RUN_ENTRY = "run_entry"
    const val COMMUNITY = "community"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val ABOUT = "about"
    const val CREATE_POST = "create_post/{runId}"
    const val POST_DETAIL = "post_detail/{postId}"
    const val ROUTE_DETAIL = "route_detail/{routeId}"
    const val EVENTS = "events"
    const val PREMIUM = "premium"

    /** Build create_post route with a specific runId. */
    fun createPostRoute(runId: String) = "create_post/$runId"

    /** Build post_detail route with a specific postId. */
    fun postDetailRoute(postId: String) = "post_detail/$postId"

    /** Build route_detail route with a specific routeId. */
    fun routeDetailRoute(routeId: String) = "route_detail/$routeId"
}
