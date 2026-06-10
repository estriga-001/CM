package com.drivepulse.domain.repository

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Comment
import com.drivepulse.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getFeedPosts(): Flow<AppResult<List<Post>>>
    fun getUserPosts(userId: String): Flow<AppResult<List<Post>>>
    suspend fun createPost(post: Post, mediaBytes: ByteArray?): AppResult<Unit>
    suspend fun toggleLike(postId: String, userId: String): AppResult<Unit>
    suspend fun addComment(postId: String, userId: String, username: String, userProfileImage: String?, text: String): AppResult<Unit>
    fun getComments(postId: String): Flow<AppResult<List<Comment>>>
    suspend fun checkHasLiked(postId: String, userId: String): AppResult<Boolean>
}
