package com.drivepulse.data.remote.dto

import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.model.MediaType
import com.drivepulse.domain.model.Post

data class PostDto(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String? = null,
    val description: String = "",
    val runId: String? = null,
    val distanceMeters: Float = 0f,
    val durationSeconds: Long = 0L,
    val avgSpeedKmh: Float = 0f,
    val runCoordinates: List<Coordinate> = emptyList(),
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val tags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = 0L
)

fun PostDto.toDomain(): Post {
    return Post(
        id = id,
        userId = userId,
        username = username,
        userProfileImage = userProfileImage,
        description = description,
        runId = runId,
        distanceMeters = distanceMeters,
        durationSeconds = durationSeconds,
        avgSpeedKmh = avgSpeedKmh,
        runCoordinates = runCoordinates,
        mediaUrl = mediaUrl,
        mediaType = mediaType?.let {
            try {
                MediaType.valueOf(it)
            } catch (e: Exception) {
                null
            }
        },
        tags = tags,
        likesCount = likesCount,
        commentsCount = commentsCount,
        createdAt = createdAt
    )
}

fun Post.toDto(): PostDto {
    return PostDto(
        id = id,
        userId = userId,
        username = username,
        userProfileImage = userProfileImage,
        description = description,
        runId = runId,
        distanceMeters = distanceMeters,
        durationSeconds = durationSeconds,
        avgSpeedKmh = avgSpeedKmh,
        runCoordinates = runCoordinates,
        mediaUrl = mediaUrl,
        mediaType = mediaType?.name,
        tags = tags,
        likesCount = likesCount,
        commentsCount = commentsCount,
        createdAt = createdAt
    )
}
