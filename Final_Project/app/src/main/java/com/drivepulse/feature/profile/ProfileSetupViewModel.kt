package com.drivepulse.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.usecase.profile.CheckUsernameUseCase
import com.drivepulse.domain.usecase.profile.CompleteOnboardingUseCase
import com.drivepulse.domain.validation.CarYearValidator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UsernameState {
    object Idle : UsernameState()
    object Checking : UsernameState()
    object Available : UsernameState()
    data class Unavailable(val reason: String) : UsernameState()
}

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState: StateFlow<OnboardingUiState> = _uiState

    private val _usernameState = MutableStateFlow<UsernameState>(UsernameState.Idle)
    val usernameState: StateFlow<UsernameState> = _usernameState

    private var usernameCheckJob: Job? = null

    fun checkUsernameAvailability(username: String) {
        val normalized = username.trim().lowercase()
        if (normalized.length < 3) {
            _usernameState.value = UsernameState.Unavailable("Too short")
            return
        }
        if (!normalized.matches(Regex("^[a-z0-9_]+$"))) {
            _usernameState.value = UsernameState.Unavailable("Invalid chars")
            return
        }

        usernameCheckJob?.cancel()
        usernameCheckJob = viewModelScope.launch {
            _usernameState.value = UsernameState.Checking
            delay(500) // Debounce

            val isAvailable = checkUsernameUseCase(normalized)
            if (isAvailable) {
                _usernameState.value = UsernameState.Available
            } else {
                _usernameState.value = UsernameState.Unavailable("Taken")
            }
        }
    }

    fun submitOnboarding(
        username: String,
        firstName: String,
        lastName: String,
        carBrand: String,
        carModel: String,
        carYear: Int
    ) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            _uiState.value = OnboardingUiState.Error("User not authenticated.")
            return
        }

        if (_usernameState.value !is UsernameState.Available) {
            _uiState.value = OnboardingUiState.Error("Please choose a valid and available username.")
            return
        }
        if (!CarYearValidator.isValid(carYear)) {
            _uiState.value = OnboardingUiState.Error(
                "Car year must be between ${CarYearValidator.MIN_YEAR} and ${CarYearValidator.maxYear}."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading

            val result = completeOnboardingUseCase(
                userId = uid,
                username = username,
                firstName = firstName,
                lastName = lastName,
                carBrand = carBrand,
                carModel = carModel,
                carYear = carYear
            )

            when (result) {
                is AppResult.Success -> {
                    _uiState.value = OnboardingUiState.Success
                }
                is AppResult.Error -> {
                    _uiState.value = OnboardingUiState.Error(result.error.message)
                }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        firebaseAuth.signOut()
        onSuccess()
    }
}
