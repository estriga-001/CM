package com.drivepulse.data.repository

import com.drivepulse.core.common.AppError
import com.drivepulse.core.common.AppResult
import com.drivepulse.data.remote.dto.UserDto
import com.drivepulse.data.remote.dto.toDomain
import com.drivepulse.data.remote.dto.toDto
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection("users")
    private val usernamesCollection = firestore.collection("usernames")

    override fun getUserProfile(userId: String): Flow<AppResult<User>> = callbackFlow {
        val listenerRegistration = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AppResult.Error(AppError(error.localizedMessage ?: "Failed to get user profile", error)))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val userDto = snapshot.toObject(UserDto::class.java)
                    if (userDto != null) {
                        trySend(AppResult.Success(userDto.toDomain()))
                    } else {
                        trySend(AppResult.Error(AppError("User profile is null")))
                    }
                } else {
                    // O documento não existe. Vamos tentar criá-lo.
                    val newUserDto = UserDto(
                        id = userId,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    usersCollection.document(userId).set(newUserDto)
                        .addOnFailureListener { e ->
                            trySend(AppResult.Error(AppError("User profile not found. Create failed: ${e.message}")))
                        }
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun updateUserProfile(user: User): AppResult<Unit> {
        return try {
            val userDto = user.toDto().copy(updatedAt = System.currentTimeMillis())
            usersCollection.document(user.id).set(userDto).await()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError(e.localizedMessage ?: "Failed to update profile", e))
        }
    }

    override suspend fun uploadProfileImage(userId: String, imageBytes: ByteArray): AppResult<String> {
        return try {
            // Resize and compress the image to max 300px for avatar, keeping it extremely small and fast
            val compressedBytes = compressAndResizeImage(imageBytes, 300)
            val base64String = "data:image/jpeg;base64," + android.util.Base64.encodeToString(compressedBytes, android.util.Base64.NO_WRAP)
            
            // Update the user's profile with the new image URL (Base64 string)
            val userRef = usersCollection.document(userId)
            userRef.update(
                "profileImageUrl", base64String,
                "updatedAt", System.currentTimeMillis()
            ).await()
            
            AppResult.Success(base64String)
        } catch (e: Exception) {
            AppResult.Error(AppError(e.localizedMessage ?: "Failed to upload image", e))
        }
    }

    /**
     * Checks the 'usernames' collection to see if a given username is available.
     * The username is stored lowercase to ensure case-insensitive uniqueness.
     */
    override suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            val normalizedUsername = username.trim().lowercase()
            val doc = usernamesCollection.document(normalizedUsername).get().await()
            !doc.exists()
        } catch (e: Exception) {
            // If we can't check, assume unavailable to prevent conflicts
            false
        }
    }

    /**
     * Uses a Firestore Transaction to atomically:
     * 1. Check if the username is already taken.
     * 2. Reserve it in the 'usernames' collection.
     * 3. Update the user's profile with all onboarding data.
     *
     * If the username is already taken, the transaction aborts with an error.
     */
    override suspend fun completeOnboarding(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        carBrand: String,
        carModel: String,
        carYear: Int
    ): AppResult<Unit> {
        return try {
            val normalizedUsername = username.trim().lowercase()
            val usernameDocRef = usernamesCollection.document(normalizedUsername)
            val userDocRef = usersCollection.document(userId)

            firestore.runTransaction { transaction ->
                // Step 1: Check if the username document already exists
                val usernameSnapshot = transaction.get(usernameDocRef)
                if (usernameSnapshot.exists()) {
                    throw Exception("Username '@$normalizedUsername' is already taken.")
                }

                // Step 2: Reserve the username
                transaction.set(usernameDocRef, mapOf(
                    "uid" to userId,
                    "createdAt" to System.currentTimeMillis()
                ))

                // Step 3: Update the user profile with all onboarding fields
                val now = System.currentTimeMillis()
                transaction.update(userDocRef, mapOf(
                    "username" to normalizedUsername,
                    "firstName" to firstName.trim(),
                    "lastName" to lastName.trim(),
                    "displayName" to "${firstName.trim()} ${lastName.trim()}",
                    "selectedCarBrand" to carBrand.trim(),
                    "selectedCarModel" to carModel.trim(),
                    "selectedCarYear" to carYear,
                    "updatedAt" to now
                ))
            }.await()

            AppResult.Success(Unit)
        } catch (e: Exception) {
            val message = e.localizedMessage ?: "Failed to complete onboarding"
            AppResult.Error(AppError(message, e))
        }
    }

    private fun compressAndResizeImage(imageBytes: ByteArray, maxDimension: Int): ByteArray {
        return try {
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

            var sampleSize = 1
            if (options.outWidth > maxDimension || options.outHeight > maxDimension) {
                val halfWidth = options.outWidth / 2
                val halfHeight = options.outHeight / 2
                while ((halfWidth / sampleSize) >= maxDimension && (halfHeight / sampleSize) >= maxDimension) {
                    sampleSize *= 2
                }
            }

            val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, decodeOptions) ?: return imageBytes

            // Scale precisely to maxDimension
            val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val (width, height) = if (ratio > 1) {
                    maxDimension to (maxDimension / ratio).toInt()
                } else {
                    (maxDimension * ratio).toInt() to maxDimension
                }
                android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true)
            } else {
                bitmap
            }

            val outputStream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, outputStream)
            
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()

            outputStream.toByteArray()
        } catch (e: Exception) {
            imageBytes
        }
    }
}
