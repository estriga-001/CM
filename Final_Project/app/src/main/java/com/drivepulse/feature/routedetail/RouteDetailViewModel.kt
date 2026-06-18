package com.drivepulse.feature.routedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<RouteDetailUiState>(RouteDetailUiState.Loading)
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RouteDetailEvent>()
    val events: SharedFlow<RouteDetailEvent> = _events.asSharedFlow()

    fun loadRouteDetail(postId: String) {
        viewModelScope.launch {
            when (val result = postRepository.getPost(postId)) {
                is AppResult.Success -> {
                    loadLikeState(result.data)
                }

                is AppResult.Error -> {
                    _uiState.value = RouteDetailUiState.Error(result.error.message)
                }
            }
        }
    }

    fun toggleLike() {
        val currentState = _uiState.value as? RouteDetailUiState.Success ?: return
        val userId = firebaseAuth.currentUser?.uid ?: return

        if (currentState.isLikeUpdating) {
            return
        }

        val newLikedState = !currentState.hasLiked
        val likesChange = if (newLikedState) 1 else -1
        val updatedPost = currentState.post.copy(
            likesCount = (currentState.post.likesCount + likesChange).coerceAtLeast(0)
        )

        _uiState.value = currentState.copy(
            post = updatedPost,
            hasLiked = newLikedState,
            isLikeUpdating = true
        )

        viewModelScope.launch {
            when (val result = postRepository.toggleLike(currentState.post.id, userId)) {
                is AppResult.Success -> {
                    _uiState.value = currentState.copy(
                        post = updatedPost,
                        hasLiked = newLikedState,
                        isLikeUpdating = false
                    )
                    _events.emit(RouteDetailEvent.LikeUpdated(newLikedState))
                }

                is AppResult.Error -> {
                    _uiState.value = currentState
                    _events.emit(RouteDetailEvent.Error(result.error.message))
                }
            }
        }
    }

    private suspend fun loadLikeState(post: Post) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            _uiState.value = RouteDetailUiState.Success(
                post = post,
                hasLiked = false,
                canLike = false
            )
            return
        }

        val hasLiked = when (val result = postRepository.checkHasLiked(post.id, userId)) {
            is AppResult.Success -> result.data
            is AppResult.Error -> false
        }

        _uiState.value = RouteDetailUiState.Success(
            post = post,
            hasLiked = hasLiked,
            canLike = true
        )
    }
}

sealed interface RouteDetailUiState {
    data object Loading : RouteDetailUiState

    data class Success(
        val post: Post,
        val hasLiked: Boolean,
        val canLike: Boolean,
        val isLikeUpdating: Boolean = false
    ) : RouteDetailUiState

    data class Error(val message: String) : RouteDetailUiState
}

sealed interface RouteDetailEvent {
    data class LikeUpdated(val liked: Boolean) : RouteDetailEvent
    data class Error(val message: String) : RouteDetailEvent
}
