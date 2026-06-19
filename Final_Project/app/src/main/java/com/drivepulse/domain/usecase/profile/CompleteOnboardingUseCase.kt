package com.drivepulse.domain.usecase.profile

import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.repository.UserRepository
import com.drivepulse.domain.validation.CarYearValidator
import javax.inject.Inject

/**
 * Use Case: Completa o onboarding de um novo utilizador.
 * Reserva o username atomicamente e guarda todos os dados do perfil inicial.
 *
 * Camada: Domain
 * Feature: Profile / Onboarding
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * @param userId the authenticated user's UID.
     * @param username the chosen @handle.
     * @param firstName the user's first name.
     * @param lastName the user's last name.
     * @param carBrand the brand of the user's car.
     * @param carModel the model of the user's car.
     * @param carYear the year of the user's car.
     * @return AppResult.Success if onboarding completed, AppResult.Error if username is taken or other failure.
     */
    suspend operator fun invoke(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        carBrand: String,
        carModel: String,
        carYear: Int
    ): AppResult<Unit> {
        // Input validation
        val normalizedUsername = username.trim().lowercase()
        if (normalizedUsername.length < 3) {
            return AppResult.Error(com.drivepulse.core.common.AppError("Username must be at least 3 characters."))
        }
        if (!normalizedUsername.matches(Regex("^[a-z0-9_]+$"))) {
            return AppResult.Error(com.drivepulse.core.common.AppError("Username can only contain letters, numbers and underscores."))
        }
        if (firstName.isBlank()) {
            return AppResult.Error(com.drivepulse.core.common.AppError("First name is required."))
        }
        if (lastName.isBlank()) {
            return AppResult.Error(com.drivepulse.core.common.AppError("Last name is required."))
        }
        if (carBrand.isBlank()) {
            return AppResult.Error(com.drivepulse.core.common.AppError("Car brand is required."))
        }
        if (carModel.isBlank()) {
            return AppResult.Error(com.drivepulse.core.common.AppError("Car model is required."))
        }
        if (!CarYearValidator.isValid(carYear)) {
            return AppResult.Error(
                com.drivepulse.core.common.AppError(
                    "Car year must be between ${CarYearValidator.MIN_YEAR} and ${CarYearValidator.maxYear}."
                )
            )
        }

        return userRepository.completeOnboarding(
            userId = userId,
            username = normalizedUsername,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            carBrand = carBrand.trim(),
            carModel = carModel.trim(),
            carYear = carYear
        )
    }
}
