package com.drivepulse.data.local.database

/**
 * Aggregated values returned directly by Room for the profile summary.
 */
data class RunStatisticsProjection(
    val totalRuns: Int,
    val totalDistanceMeters: Double,
    val totalDurationSeconds: Long
)
