package com.drivepulse.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val currentUserId = firebaseAuth.currentUser?.uid

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    val uiState: StateFlow<CommunityUiState> = postRepository.getFeedPosts()
        .map { result ->
            when (result) {
                is AppResult.Success -> CommunityUiState.Success(result.data)
                is AppResult.Error -> {
                    Timber.e(result.error.throwable, "Erro ao carregar o feed da comunidade")
                    CommunityUiState.Error("Não foi possível carregar as publicações. Verifica a tua ligação à internet.")
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommunityUiState.Loading
        )

    init {
        observeLikes()
    }

    private fun observeLikes() {
        viewModelScope.launch {
            uiState.collectLatest { state ->
                if (state is CommunityUiState.Success) {
                    val uid = currentUserId ?: return@collectLatest
                    state.posts.forEach { post ->
                        launch {
                            val result = postRepository.checkHasLiked(post.id, uid)
                            if (result is AppResult.Success && result.data) {
                                _likedPostIds.update { it + post.id }
                            }
                        }
                    }
                }
            }
        }
    }

    fun toggleLike(postId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            // Optimistic update
            val wasLiked = _likedPostIds.value.contains(postId)
            if (wasLiked) {
                _likedPostIds.update { it - postId }
            } else {
                _likedPostIds.update { it + postId }
            }
            
            val result = postRepository.toggleLike(postId, uid)
            if (result is AppResult.Error) {
                // Revert on error
                if (wasLiked) {
                    _likedPostIds.update { it + postId }
                } else {
                    _likedPostIds.update { it - postId }
                }
            }
        }
    }
}
