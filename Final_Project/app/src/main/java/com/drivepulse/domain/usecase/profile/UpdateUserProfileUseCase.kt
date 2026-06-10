package com.drivepulse.domain.usecase.profile

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): AppResult<Unit> {
        return userRepository.updateUserProfile(user)
    }
}
