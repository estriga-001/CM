package com.drivepulse.domain.usecase.profile

import com.drivepulse.core.common.AppResult
import com.drivepulse.core.common.AppError
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.UserRepository
import com.drivepulse.domain.validation.CarYearValidator
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): AppResult<Unit> {
        val hasCarData = user.selectedCarBrand.isNotBlank() ||
            user.selectedCarModel.isNotBlank() ||
            user.selectedCarYear != 0

        if (hasCarData && !CarYearValidator.isValid(user.selectedCarYear)) {
            return AppResult.Error(
                AppError(
                    "Car year must be between ${CarYearValidator.MIN_YEAR} and ${CarYearValidator.maxYear}."
                )
            )
        }

        return userRepository.updateUserProfile(user)
    }
}
