package com.drivepulse.domain.usecase.profile

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.repository.UserRepository
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, imageBytes: ByteArray): AppResult<String> {
        return userRepository.uploadProfileImage(userId, imageBytes)
    }
}
