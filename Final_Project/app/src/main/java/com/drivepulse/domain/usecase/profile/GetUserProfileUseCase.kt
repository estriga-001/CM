package com.drivepulse.domain.usecase.profile

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<AppResult<User>> {
        return userRepository.getUserProfile(userId)
    }
}
