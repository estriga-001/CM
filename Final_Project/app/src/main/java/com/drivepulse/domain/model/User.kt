/**
 * Domain model representing a DrivePulse user.
 *
 * Camada: Domain
 * Feature: Auth/Profile
 */
package com.drivepulse.domain.model

/**
 * Core entity for an authenticated user in the system.
 * Contains no references to Android or Firebase SDKs.
 *
 * @property id unique identifier for the user (from Auth system).
 * @property email the user's email address.
 * @property username the user's unique @handle (lowercase, no spaces).
 * @property firstName the user's first name.
 * @property lastName the user's last name.
 * @property displayName the user's display name (legacy/compat field).
 * @property profileImageUrl optional URL for the user's avatar.
 * @property selectedCarBrand the chosen brand of the user's car.
 * @property selectedCarModel the chosen model of the user's car.
 * @property selectedCarYear the year of the user's car.
 * @property generatedCarImageUrl a generated URL or local URI for the car avatar.
 * @property bio a short biography of the user.
 * @property isPremium whether the user has an active premium subscription.
 * @property totalKm total kilometers driven.
 * @property totalRuns total number of recorded runs.
 * @property followersCount number of followers.
 * @property followingCount number of users this user follows.
 * @property friendsCount number of mutual friends.
 * @property createdAt timestamp when the user was created.
 * @property updatedAt timestamp of last profile update.
 */
data class User(
    val id: String,
    val email: String,
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val profileImageUrl: String? = null,
    val selectedCarBrand: String = "",
    val selectedCarModel: String = "",
    val selectedCarYear: Int = 0,
    val generatedCarImageUrl: String? = null,
    val bio: String = "",
    val isPremium: Boolean = false,
    val totalKm: Double = 0.0,
    val totalRuns: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val friendsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
