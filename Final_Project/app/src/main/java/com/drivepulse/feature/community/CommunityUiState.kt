package com.drivepulse.feature.community

import com.drivepulse.domain.model.Post

sealed interface CommunityUiState {
    data object Loading : CommunityUiState
    data class Success(
        val posts: List<Post>,
        val isLoadingMore: Boolean,
        val hasMore: Boolean,
        val loadMoreError: String? = null
    ) : CommunityUiState
    data class Error(val message: String) : CommunityUiState
}
