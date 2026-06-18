package com.drivepulse.domain.repository

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Comment
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.PostPage
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getFeedPosts(limit: Long? = null): Flow<AppResult<List<Post>>>
    fun getUserPosts(
        userId: String,
        limit: Long? = null
    ): Flow<AppResult<List<Post>>>
    suspend fun getFeedPostsPage(
        pageSize: Int,
        afterPostId: String? = null
    ): AppResult<PostPage>
    suspend fun getUserPostsPage(
        userId: String,
        pageSize: Int,
        afterPostId: String? = null
    ): AppResult<PostPage>
    suspend fun getPost(postId: String): AppResult<Post>
    suspend fun createPost(post: Post, mediaBytes: ByteArray?): AppResult<Unit>
    suspend fun toggleLike(postId: String, userId: String): AppResult<Unit>
    suspend fun addComment(postId: String, userId: String, username: String, userProfileImage: String?, text: String): AppResult<Unit>
    fun getComments(postId: String): Flow<AppResult<List<Comment>>>
    suspend fun checkHasLiked(postId: String, userId: String): AppResult<Boolean>
}
