package com.drivepulse.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val username: String,
    val userProfileImage: String?,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
