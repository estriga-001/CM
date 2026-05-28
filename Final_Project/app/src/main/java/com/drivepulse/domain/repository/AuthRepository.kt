/**
 * Repository interface for Authentication operations.
 *
 * Camada: Domain
 * Feature: Auth
 */
package com.drivepulse.domain.repository

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Contract for all authentication related data operations.
 * Must be implemented by the Data layer (e.g. Firebase).
 */
interface AuthRepository {
    
    /**
     * Authenticates a user with email and password.
     *
     * @param email user email.
     * @param password user password.
     * @return AppResult containing the authenticated User or an AppError.
     */
    suspend fun login(email: String, password: String): AppResult<User>
    
    /**
     * Registers a new user with email and password.
     *
     * @param email new user email.
     * @param password new user password.
     * @return AppResult containing the newly created User or an AppError.
     */
    suspend fun register(email: String, password: String): AppResult<User>

    /**
     * Authenticates a user with a Google ID token.
     *
     * @param idToken the Google ID token obtained from Credential Manager.
     * @return AppResult containing the authenticated User or an AppError.
     */
    suspend fun signInWithGoogle(idToken: String): AppResult<User>
    
    /**
     * Signs out the current user.
     */
    suspend fun logout()
    
    /**
     * Observes the current authentication state.
     *
     * @return a Flow emitting the current User if logged in, or null if logged out.
     */
    fun observeAuthState(): Flow<User?>
}
