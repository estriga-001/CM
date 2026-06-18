package com.drivepulse.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Comment
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId.asStateFlow()

    private val _commentsState = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _commentsState.asStateFlow()

    private var commentsJob: Job? = null
    private var firstPageJob: Job? = null
    private var nextPageCursor: String? = null
    private var firstPagePosts: List<Post> = emptyList()
    private var olderPosts: List<Post> = emptyList()
    private val checkedLikePostIds = mutableSetOf<String>()

    init {
        observeFirstPage()
        observeCurrentUserProfile()
    }

    fun retryInitialLoad() {
        observeFirstPage()
    }

    fun loadMorePosts() {
        val currentState = _uiState.value as? CommunityUiState.Success ?: return
        val cursor = nextPageCursor ?: return

        if (currentState.isLoadingMore || !currentState.hasMore) {
            return
        }

        _uiState.value = currentState.copy(
            isLoadingMore = true,
            loadMoreError = null
        )

        viewModelScope.launch {
            when (
                val result = postRepository.getFeedPostsPage(
                    pageSize = PAGE_SIZE,
                    afterPostId = cursor
                )
            ) {
                is AppResult.Success -> {
                    val newPosts = result.data.posts
                    olderPosts = mergePosts(olderPosts, newPosts)
                    val mergedPosts = combineLoadedPosts()
                    nextPageCursor = result.data.nextCursor
                    _uiState.value = CommunityUiState.Success(
                        posts = mergedPosts,
                        isLoadingMore = false,
                        hasMore = nextPageCursor != null
                    )
                    loadLikeStates(newPosts)
                }

                is AppResult.Error -> {
                    Timber.e(result.error.throwable, "Failed to load more community posts")
                    _uiState.value = currentState.copy(
                        isLoadingMore = false,
                        loadMoreError = result.error.message
                    )
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
        val userId = currentUserId ?: return
        val profile = _currentUserProfile.value

        if (text.isBlank()) {
            return
        }

        viewModelScope.launch {
            val result = postRepository.addComment(
                postId = postId,
                userId = userId,
                username = profile?.username ?: "user",
                userProfileImage = profile?.profileImageUrl,
                text = text
            )

            if (result is AppResult.Error) {
                Timber.e(result.error.throwable, "Failed to add comment")
            }
        }
    }

    fun toggleLike(postId: String) {
        val userId = currentUserId ?: return
        val wasLiked = _likedPostIds.value.contains(postId)
        val countChange = if (wasLiked) -1 else 1

        updateLikeState(postId, liked = !wasLiked, countChange = countChange)

        viewModelScope.launch {
            val result = postRepository.toggleLike(postId, userId)
            if (result is AppResult.Error) {
                updateLikeState(
                    postId = postId,
                    liked = wasLiked,
                    countChange = -countChange
                )
            }
        }
    }

    private fun observeFirstPage() {
        firstPageJob?.cancel()
        _uiState.value = CommunityUiState.Loading
        _likedPostIds.value = emptySet()
        checkedLikePostIds.clear()
        firstPagePosts = emptyList()
        olderPosts = emptyList()
        nextPageCursor = null

        firstPageJob = viewModelScope.launch {
            postRepository.getFeedPosts(limit = (PAGE_SIZE + 1).toLong())
                .collectLatest { result ->
                    when (result) {
                        is AppResult.Success -> {
                            updateFirstPage(result.data)
                        }

                        is AppResult.Error -> {
                            Timber.e(result.error.throwable, "Failed to load community feed")
                            val currentState = _uiState.value
                            if (currentState !is CommunityUiState.Success) {
                                _uiState.value = CommunityUiState.Error(result.error.message)
                            }
                        }
                    }
                }
        }
    }

    private fun updateFirstPage(posts: List<Post>) {
        val visiblePosts = posts.take(PAGE_SIZE)

        if (olderPosts.isNotEmpty()) {
            val currentFirstPageIds = visiblePosts.mapTo(mutableSetOf()) { post ->
                post.id
            }
            val oldestVisiblePostTime = visiblePosts.minOfOrNull { post ->
                post.createdAt
            }
            val shiftedPosts = firstPagePosts.filter { post ->
                post.id !in currentFirstPageIds &&
                    oldestVisiblePostTime != null &&
                    post.createdAt <= oldestVisiblePostTime
            }
            olderPosts = mergePosts(shiftedPosts, olderPosts)
        }

        firstPagePosts = visiblePosts
        val combinedPosts = combineLoadedPosts()

        if (olderPosts.isEmpty()) {
            nextPageCursor = if (posts.size > PAGE_SIZE) {
                visiblePosts.lastOrNull()?.id
            } else {
                null
            }
        }

        _uiState.value = CommunityUiState.Success(
            posts = combinedPosts,
            isLoadingMore = false,
            hasMore = nextPageCursor != null
        )
        loadLikeStates(visiblePosts)
    }

    private fun observeCurrentUserProfile() {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            userRepository.getUserProfile(userId).collectLatest { result ->
                if (result is AppResult.Success) {
                    _currentUserProfile.value = result.data
                }
            }
        }
    }

    private fun loadLikeStates(posts: List<Post>) {
        val userId = currentUserId ?: return

        posts.filter { post ->
            checkedLikePostIds.add(post.id)
        }.forEach { post ->
            viewModelScope.launch {
                val result = postRepository.checkHasLiked(post.id, userId)
                if (result is AppResult.Success && result.data) {
                    _likedPostIds.update { likedIds ->
                        likedIds + post.id
                    }
                }
            }
        }
    }

    private fun updateLikeState(
        postId: String,
        liked: Boolean,
        countChange: Int
    ) {
        _likedPostIds.update { likedIds ->
            if (liked) {
                likedIds + postId
            } else {
                likedIds - postId
            }
        }

        val currentState = _uiState.value as? CommunityUiState.Success ?: return
        val updatedPosts = currentState.posts.map { post ->
            if (post.id == postId) {
                post.copy(
                    likesCount = (post.likesCount + countChange).coerceAtLeast(0)
                )
            } else {
                post
            }
        }
        firstPagePosts = updatePostLikeCount(firstPagePosts, postId, countChange)
        olderPosts = updatePostLikeCount(olderPosts, postId, countChange)
        _uiState.value = currentState.copy(posts = updatedPosts)
    }

    private fun updatePostLikeCount(
        posts: List<Post>,
        postId: String,
        countChange: Int
    ): List<Post> {
        return posts.map { post ->
            if (post.id == postId) {
                post.copy(
                    likesCount = (post.likesCount + countChange).coerceAtLeast(0)
                )
            } else {
                post
            }
        }
    }

    private fun combineLoadedPosts(): List<Post> {
        return mergePosts(firstPagePosts, olderPosts)
            .sortedByDescending { post -> post.createdAt }
    }

    private fun mergePosts(
        currentPosts: List<Post>,
        newPosts: List<Post>
    ): List<Post> {
        val existingIds = currentPosts.mapTo(mutableSetOf()) { post ->
            post.id
        }
        val uniqueNewPosts = newPosts.filter { post ->
            existingIds.add(post.id)
        }
        return currentPosts + uniqueNewPosts
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}
