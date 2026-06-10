package com.drivepulse.domain.repository

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(userId: String): Flow<AppResult<User>>
    suspend fun updateUserProfile(user: User): AppResult<Unit>
    suspend fun uploadProfileImage(userId: String, imageBytes: ByteArray): AppResult<String>

    /**
     * Checks if the given username is available (not taken by another user).
     * @param username the username to check (will be lowercased internally).
     * @return true if available, false if already taken.
     */
    suspend fun isUsernameAvailable(username: String): Boolean

    /**
     * Completes the onboarding process atomically:
     * 1. Reserves the username in the 'usernames' collection.
     * 2. Updates the user's profile document with all onboarding data.
     * Uses a Firestore Transaction to prevent race conditions.
     */
    suspend fun completeOnboarding(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        carBrand: String,
        carModel: String,
        carYear: Int
    ): AppResult<Unit>
}
