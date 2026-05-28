package com.drivepulse.feature.community

import com.drivepulse.domain.model.Run

sealed interface CommunityUiState {
    data object Loading : CommunityUiState
    data class Success(val runs: List<Run>) : CommunityUiState
    data class Error(val message: String) : CommunityUiState
}
