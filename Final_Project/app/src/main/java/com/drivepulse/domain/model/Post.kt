package com.drivepulse.domain.model

/**
 * Representa uma publicação na Comunidade (Community Feed).
 * Pode ser um simples post com foto/vídeo, ou estar associado a uma Run gravada.
 */
data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val userProfileImage: String?,
    val description: String,
    val runId: String?, // Se null, é apenas um post normal sem run associada
    val mediaUrl: String?,
    val mediaType: MediaType?,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MediaType {
    IMAGE,
    VIDEO
}
