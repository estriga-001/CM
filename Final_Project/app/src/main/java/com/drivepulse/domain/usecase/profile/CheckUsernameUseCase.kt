package com.drivepulse.domain.usecase.profile

import com.drivepulse.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use Case: Verifica se um @username está disponível.
 *
 * Camada: Domain
 * Feature: Profile / Onboarding
 */
class CheckUsernameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * @param username the username to check.
     * @return true if the username is available, false if already taken.
     */
    suspend operator fun invoke(username: String): Boolean {
        val normalized = username.trim().lowercase()
        // Basic validation: must be 3-20 chars, alphanumeric + underscores only
        if (normalized.length < 3 || normalized.length > 20) return false
        if (!normalized.matches(Regex("^[a-z0-9_]+$"))) return false
        return userRepository.isUsernameAvailable(normalized)
    }
}
