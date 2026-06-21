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
 * @property bio a short biography of the user.
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
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
