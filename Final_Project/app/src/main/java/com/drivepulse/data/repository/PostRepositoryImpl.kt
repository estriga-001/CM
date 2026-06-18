package com.drivepulse.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.drivepulse.core.common.AppError
import com.drivepulse.core.common.AppResult
import com.drivepulse.data.remote.dto.CommentDto
import com.drivepulse.data.remote.dto.PostDto
import com.drivepulse.data.remote.dto.toDomain
import com.drivepulse.data.remote.dto.toDto
import com.drivepulse.domain.model.Comment
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.PostPage
import com.drivepulse.domain.repository.PostRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    private val postsCollection = firestore.collection("posts")

    override fun getFeedPosts(limit: Long?): Flow<AppResult<List<Post>>> = callbackFlow {
        var query: Query = postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)

        if (limit != null) {
            query = query.limit(limit)
        }

        val listener = query
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AppResult.Error(AppError(error.localizedMessage ?: "Error fetching posts", error)))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PostDto::class.java)?.toDomain()
                    }
                    trySend(AppResult.Success(posts))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getUserPosts(
        userId: String,
        limit: Long?
    ): Flow<AppResult<List<Post>>> = callbackFlow {
        var query: Query = postsCollection
            .whereEqualTo("userId", userId)

        if (limit != null) {
            query = query
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
        }

        val listener = query
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AppResult.Error(AppError(error.localizedMessage ?: "Error fetching user posts", error)))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PostDto::class.java)?.toDomain()
                    }.sortedByDescending { it.createdAt }
                    trySend(AppResult.Success(posts))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getFeedPostsPage(
        pageSize: Int,
        afterPostId: String?
    ): AppResult<PostPage> {
        var query: Query = postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)

        return getPostsPage(
            query = query,
            pageSize = pageSize,
            afterPostId = afterPostId
        )
    }

    override suspend fun getUserPostsPage(
        userId: String,
        pageSize: Int,
        afterPostId: String?
    ): AppResult<PostPage> {
        val query = postsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        return getPostsPage(
            query = query,
            pageSize = pageSize,
            afterPostId = afterPostId
        )
    }

    override suspend fun getPost(postId: String): AppResult<Post> {
        return try {
            val document = postsCollection.document(postId).get().await()
            val post = document.toObject(PostDto::class.java)?.toDomain()

            if (post != null) {
                AppResult.Success(post)
            } else {
                AppResult.Error(AppError("Route not found"))
            }
        } catch (e: Exception) {
            AppResult.Error(
                AppError(
                    message = e.localizedMessage ?: "Failed to load route details",
                    throwable = e
                )
            )
        }
    }

    override suspend fun createPost(post: Post, mediaBytes: ByteArray?): AppResult<Unit> {
        return try {
            var mediaUrl: String? = null
            
            if (mediaBytes != null && mediaBytes.isNotEmpty()) {
                // Resize and compress the image to max 600px for post media, keeping it highly optimized
                val compressedBytes = compressAndResizeImage(mediaBytes, 600)
                mediaUrl = "data:image/jpeg;base64," + android.util.Base64.encodeToString(compressedBytes, android.util.Base64.NO_WRAP)
            }

            val postDto = post.copy(mediaUrl = mediaUrl).toDto()
            postsCollection.document(post.id).set(postDto).await()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError(e.localizedMessage ?: "Failed to create post", e))
        }
    }

    override suspend fun toggleLike(postId: String, userId: String): AppResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val likeRef = postRef.collection("likes").document(userId)

            firestore.runTransaction { transaction ->
                val likeSnap = transaction.get(likeRef)
                if (likeSnap.exists()) {
                    transaction.delete(likeRef)
                    transaction.update(postRef, "likesCount", FieldValue.increment(-1))
                } else {
                    transaction.set(likeRef, mapOf(
                        "uid" to userId,
                        "createdAt" to System.currentTimeMillis()
                    ))
                    transaction.update(postRef, "likesCount", FieldValue.increment(1))
                }
            }.await()
            
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError(e.localizedMessage ?: "Failed to toggle like", e))
        }
    }

    override suspend fun addComment(
        postId: String,
        userId: String,
        username: String,
        userProfileImage: String?,
        text: String
    ): AppResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val commentRef = postRef.collection("comments").document()
            
            val commentDto = CommentDto(
                id = commentRef.id,
                postId = postId,
                userId = userId,
                username = username,
                userProfileImage = userProfileImage,
                text = text,
                createdAt = System.currentTimeMillis()
            )

            firestore.runTransaction { transaction ->
                transaction.set(commentRef, commentDto)
                transaction.update(postRef, "commentsCount", FieldValue.increment(1))
            }.await()

            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError(e.localizedMessage ?: "Failed to add comment", e))
        }
    }

    override fun getComments(postId: String): Flow<AppResult<List<Comment>>> = callbackFlow {
        val listener = postsCollection.document(postId).collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(AppResult.Error(AppError(error.localizedMessage ?: "Error fetching comments", error)))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CommentDto::class.java)?.toDomain()
                    }
                    trySend(AppResult.Success(comments))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun checkHasLiked(postId: String, userId: String): AppResult<Boolean> {
        return try {
            val likeRef = postsCollection.document(postId).collection("likes").document(userId)
            val snap = likeRef.get().await()
            AppResult.Success(snap.exists())
        } catch (e: Exception) {
            AppResult.Error(AppError(e.localizedMessage ?: "Failed to check like status", e))
        }
    }

    private suspend fun getPostsPage(
        query: Query,
        pageSize: Int,
        afterPostId: String?
    ): AppResult<PostPage> {
        return try {
            val safePageSize = pageSize.coerceAtLeast(1)
            var pagedQuery = query

            if (afterPostId != null) {
                val cursorDocument = postsCollection.document(afterPostId).get().await()
                if (!cursorDocument.exists()) {
                    return AppResult.Error(AppError("Pagination cursor not found"))
                }
                pagedQuery = pagedQuery.startAfter(cursorDocument)
            }

            val snapshot = pagedQuery
                .limit((safePageSize + 1).toLong())
                .get()
                .await()

            val pageDocuments = snapshot.documents.take(safePageSize)
            val posts = pageDocuments.mapNotNull { document ->
                document.toObject(PostDto::class.java)?.toDomain()
            }
            val hasMore = snapshot.documents.size > safePageSize
            val nextCursor = if (hasMore) {
                pageDocuments.lastOrNull()?.id
            } else {
                null
            }

            AppResult.Success(
                PostPage(
                    posts = posts,
                    nextCursor = nextCursor
                )
            )
        } catch (exception: Exception) {
            AppResult.Error(
                AppError(
                    message = exception.localizedMessage ?: "Failed to load posts page",
                    throwable = exception
                )
            )
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
