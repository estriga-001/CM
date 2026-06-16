package com.drivepulse.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Comment
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val currentUserId = firebaseAuth.currentUser?.uid

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId.asStateFlow()

    private val _commentsState = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _commentsState.asStateFlow()

    private var commentsJob: Job? = null

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
        observeCurrentUserProfile()
    }

    private fun observeCurrentUserProfile() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            userRepository.getUserProfile(uid).collectLatest { result ->
                if (result is AppResult.Success) {
                    _currentUserProfile.value = result.data
                }
            }
        }
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

    fun selectPostForComments(postId: String?) {
        _selectedPostId.value = postId
        commentsJob?.cancel()
        if (postId == null) {
            _commentsState.value = emptyList()
            return
        }

        commentsJob = viewModelScope.launch {
            postRepository.getComments(postId).collectLatest { result ->
                if (result is AppResult.Success) {
                    _commentsState.value = result.data
                } else if (result is AppResult.Error) {
                    Timber.e(result.error.throwable, "Error loading comments for post $postId")
                }
            }
        }
    }

    fun addComment(text: String) {
        val postId = _selectedPostId.value ?: return
        val uid = currentUserId ?: return
        val profile = _currentUserProfile.value
        val username = profile?.username ?: "user"
        val profileImage = profile?.profileImageUrl

        if (text.isBlank()) return

        viewModelScope.launch {
            val result = postRepository.addComment(
                postId = postId,
                userId = uid,
                username = username,
                userProfileImage = profileImage,
                text = text
            )
            if (result is AppResult.Error) {
                Timber.e(result.error.throwable, "Failed to add comment")
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
