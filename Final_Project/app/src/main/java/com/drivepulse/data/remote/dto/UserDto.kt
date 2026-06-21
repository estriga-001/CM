package com.drivepulse.data.remote.dto

import com.drivepulse.domain.model.User

/**
 * Data Transfer Object for Firestore 'users' collection.
 * Includes the profile fields currently persisted by the app.
 */
data class UserDto(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val profileImageUrl: String? = null,
    val selectedCarBrand: String = "",
    val selectedCarModel: String = "",
    val selectedCarYear: Int = 0,
    val bio: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        username = username,
        firstName = firstName,
        lastName = lastName,
        displayName = displayName,
        profileImageUrl = profileImageUrl,
        selectedCarBrand = selectedCarBrand,
        selectedCarModel = selectedCarModel,
        selectedCarYear = selectedCarYear,
        bio = bio,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        firstName = firstName,
        lastName = lastName,
        displayName = displayName,
        profileImageUrl = profileImageUrl,
        selectedCarBrand = selectedCarBrand,
        selectedCarModel = selectedCarModel,
        selectedCarYear = selectedCarYear,
        bio = bio,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
