package com.drivepulse.data.remote.dto

import com.drivepulse.domain.model.User

/**
 * Data Transfer Object for Firestore 'users' collection.
 * Includes all profile fields and counters for social features.
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
    val generatedCarImageUrl: String? = null,
    val bio: String = "",
    val isPremium: Boolean = false,
    val totalKm: Double = 0.0,
    val totalRuns: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val friendsCount: Int = 0,
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
        generatedCarImageUrl = generatedCarImageUrl,
        bio = bio,
        isPremium = isPremium,
        totalKm = totalKm,
        totalRuns = totalRuns,
        followersCount = followersCount,
        followingCount = followingCount,
        friendsCount = friendsCount,
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
        generatedCarImageUrl = generatedCarImageUrl,
        bio = bio,
        isPremium = isPremium,
        totalKm = totalKm,
        totalRuns = totalRuns,
        followersCount = followersCount,
        followingCount = followingCount,
        friendsCount = friendsCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
