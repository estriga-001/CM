package com.drivepulse.feature.profile

import com.drivepulse.domain.model.User

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
