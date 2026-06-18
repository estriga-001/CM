/**
 * Application-wide constants for Intent extras and keys.
 *
 * Camada: Core / Common
 * Feature: All
 */
package com.drivepulse.core.common

/**
 * Constants used for inter-Activity communication via Intents.
 */
object Constants {

    // --- Auth Activity ---
    const val EXTRA_START_MODE = "extra_start_mode"
    const val EXTRA_SESSION_MODE = "extra_session_mode"
    const val START_MODE_LOGIN = "login"
    const val START_MODE_REGISTER = "register"
    const val START_MODE_GUEST = "guest"
    const val RESULT_AUTH_SUCCESS = 1001

    // --- Run Recorder Activity ---
    const val EXTRA_START_LOCATION = "extra_start_location"
    const val EXTRA_SELECTED_CAR_ID = "extra_selected_car_id"
    const val EXTRA_PRIVACY_MODE = "extra_privacy_mode"
    const val EXTRA_RUN_ID = "extra_run_id"
    const val EXTRA_RUN_STATUS = "extra_run_status"
    const val RUN_STATUS_DRAFT = "DRAFT"
    const val RUN_STATUS_PUBLISHED = "PUBLISHED"
    const val RUN_STATUS_DISCARDED = "DISCARDED"

    // --- Route Detail Activity ---
    const val EXTRA_ROUTE_ID = "extra_route_id"
    const val EXTRA_ROUTE_SOURCE = "extra_route_source"
    const val EXTRA_ROUTE_SAVED = "extra_route_saved"
    const val EXTRA_ROUTE_UPDATED = "extra_route_updated"
    const val EXTRA_ROUTE_LIKED = "extra_route_liked"
    const val SOURCE_MAP = "MAP"
    const val SOURCE_FEED = "FEED"
    const val SOURCE_PROFILE = "PROFILE"

    // --- Deep Links ---
    const val EXTRA_DEEP_LINK_ROUTE_ID = "extra_deep_link_route_id"

    /**
     * Converts a Base64 data URI (data:image/...) to a ByteArray for reliable loading in Coil.
     */
    fun getCoilDataModel(urlOrBase64: String?): Any? {
        if (urlOrBase64 == null) return null
        if (urlOrBase64.startsWith("data:image")) {
            val base64Content = urlOrBase64.substringAfter("base64,")
            return try {
                android.util.Base64.decode(base64Content, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                urlOrBase64
            }
        }
        return urlOrBase64
    }
}
