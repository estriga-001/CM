package com.drivepulse.data.remote.dto

import com.drivepulse.domain.model.Comment

data class CommentDto(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String? = null,
    val text: String = "",
    val createdAt: Long = 0L
)

fun CommentDto.toDomain(): Comment {
    return Comment(
        id = id,
        postId = postId,
        userId = userId,
        username = username,
        userProfileImage = userProfileImage,
        text = text,
        createdAt = createdAt
    )
}

fun Comment.toDto(): CommentDto {
    return CommentDto(
        id = id,
        postId = postId,
        userId = userId,
        username = username,
        userProfileImage = userProfileImage,
        text = text,
        createdAt = createdAt
    )
}
