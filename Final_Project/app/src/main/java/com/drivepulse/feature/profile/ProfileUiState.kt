package com.drivepulse.feature.profile

import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.User

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

sealed interface ProfilePostsUiState {
    data object Loading : ProfilePostsUiState

    data class Success(
        val posts: List<Post>,
        val isLoadingMore: Boolean,
        val hasMore: Boolean,
        val loadMoreError: String? = null
    ) : ProfilePostsUiState

    data class Error(val message: String) : ProfilePostsUiState
}
