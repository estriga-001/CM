/**
 * Firebase implementation of AuthRepository.
 *
 * Camada: Data
 * Feature: Auth
 */
package com.drivepulse.data.repository

import com.drivepulse.core.common.AppError
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import com.drivepulse.data.remote.dto.UserDto
import com.drivepulse.data.remote.dto.toDomain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val userProfile = ensureUserDocumentAndGetProfile(firebaseUser)
                AppResult.Success(userProfile)
            } else {
                AppResult.Error(AppError("User not found after login"))
            }
        } catch (e: Exception) {
            AppResult.Error(mapFirebaseError(e))
        }
    }

    override suspend fun register(email: String, password: String): AppResult<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val userProfile = ensureUserDocumentAndGetProfile(firebaseUser)
                AppResult.Success(userProfile)
            } else {
                AppResult.Error(AppError("Failed to create user"))
            }
        } catch (e: Exception) {
            AppResult.Error(mapFirebaseError(e))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AppResult<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val userProfile = ensureUserDocumentAndGetProfile(firebaseUser)
                AppResult.Success(userProfile)
            } else {
                AppResult.Error(AppError("Google Sign-In failed: No user returned"))
            }
        } catch (e: Exception) {
            AppResult.Error(mapFirebaseError(e))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun checkCurrentSession(): AppResult<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val userProfile = ensureUserDocumentAndGetProfile(firebaseUser)
                AppResult.Success(userProfile)
            } else {
                AppResult.Success(null)
            }
        } catch (e: Exception) {
            AppResult.Error(mapFirebaseError(e))
        }
    }

    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomainUser())
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Ensures the users/{uid} document exists in Firestore and returns the mapped User.
     * If not, creates a base profile using UserDto with an empty username.
     */
    private suspend fun ensureUserDocumentAndGetProfile(firebaseUser: FirebaseUser): User {
        val userRef = firestore.collection("users").document(firebaseUser.uid)
        val snapshot = userRef.get().await()
        
        if (snapshot.exists()) {
            val dto = snapshot.toObject(UserDto::class.java)
            if (dto != null) {
                return dto.toDomain()
            }
        }
        
        // If it doesn't exist or is invalid, create a new one.
        // We leave username empty to force the Onboarding flow.
        val newUserDto = UserDto(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            username = "",
            displayName = firebaseUser.displayName ?: "",
            profileImageUrl = firebaseUser.photoUrl?.toString(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userRef.set(newUserDto).await()
        return newUserDto.toDomain()
    }

    /**
     * Extension to map FirebaseUser to our Domain User.
     */
    private fun FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            email = email ?: "",
            username = displayName ?: email?.substringBefore("@") ?: "User",
            profileImageUrl = photoUrl?.toString()
        )
    }

    /**
     * Maps FirebaseAuthException to a user-friendly AppError.
     */
    private fun mapFirebaseError(e: Exception): AppError {
        val message = if (e is FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Invalid email format."
                "ERROR_WRONG_PASSWORD" -> "Incorrect password."
                "ERROR_USER_NOT_FOUND" -> "No user found with this email."
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
                "ERROR_WEAK_PASSWORD" -> "Password is too weak."
                else -> e.localizedMessage ?: "Authentication failed."
            }
        } else {
            e.localizedMessage ?: "An unexpected error occurred."
        }
        return AppError(message, e)
    }
}
