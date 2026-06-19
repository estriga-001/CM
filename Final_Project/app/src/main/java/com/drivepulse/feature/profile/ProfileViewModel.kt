/**
 * ViewModel for Profile Screen.
 *
 * Camada: Presentation
 * Feature: Profile
 *
 * Responsabilidades:
 * - Observar o perfil do utilizador autenticado via Flow.
 * - Carregar os posts publicados pelo utilizador.
 * - Gerir upload de imagem e logout.
 * - Calcular estatísticas agregadas (km totais, tempo total, nº de runs) em tempo real.
 */
package com.drivepulse.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.Run
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.domain.repository.RunRepository
import com.drivepulse.domain.usecase.auth.LogoutUseCase
import com.drivepulse.domain.usecase.profile.GetUserProfileUseCase
import com.drivepulse.domain.usecase.profile.UpdateUserProfileUseCase
import com.drivepulse.domain.usecase.profile.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Estatísticas agregadas calculadas a partir das runs locais do utilizador.
 *
 * @property totalRuns número total de runs registadas.
 * @property totalKm distância total percorrida em quilómetros.
 * @property totalMinutes tempo total de condução em minutos.
 */
data class ProfileStats(
    val totalRuns: Int = 0,
    val totalKm: Double = 0.0,
    val totalMinutes: Long = 0L
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val postRepository: PostRepository,
    private val runRepository: RunRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _userPosts = MutableStateFlow<ProfilePostsUiState>(ProfilePostsUiState.Loading)
    val userPosts: StateFlow<ProfilePostsUiState> = _userPosts.asStateFlow()

    /** Estatísticas calculadas dinamicamente a partir das runs locais. */
    private val _profileStats = MutableStateFlow(ProfileStats())
    val profileStats: StateFlow<ProfileStats> = _profileStats.asStateFlow()

    private val _savedRuns = MutableStateFlow<List<Run>>(emptyList())
    val savedRuns: StateFlow<List<Run>> = _savedRuns.asStateFlow()

    private var currentUserId: String? = null
    private var postsFirstPageJob: Job? = null
    private var savedRunsJob: Job? = null
    private var nextPostsCursor: String? = null
    private var firstPageUserPosts: List<Post> = emptyList()
    private var olderUserPosts: List<Post> = emptyList()

    init {
        observeUser()
    }

    /**
     * Observa o estado de autenticação e carrega o perfil
     * assim que o utilizador estiver autenticado.
     */
    private fun observeUser() {
        viewModelScope.launch {
            authRepository.observeAuthState().collectLatest { authUser ->
                if (authUser != null) {
                    if (currentUserId == authUser.id) {
                        return@collectLatest
                    }
                    currentUserId = authUser.id
                    loadProfile(authUser.id)
                    observeFirstUserPostsPage(authUser.id)
                    loadProfileStats(authUser.id)
                    loadSavedRuns(authUser.id)
                } else {
                    postsFirstPageJob?.cancel()
                    savedRunsJob?.cancel()
                    currentUserId = null
                    firstPageUserPosts = emptyList()
                    olderUserPosts = emptyList()
                    nextPostsCursor = null
                    _savedRuns.value = emptyList()
                    _uiState.value = ProfileUiState.Error("User not authenticated")
                    _userPosts.value = ProfilePostsUiState.Error("User not authenticated")
                }
            }
        }
    }

    /** Observa o perfil do utilizador em tempo real via Firestore snapshot. */
    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            getUserProfileUseCase(userId).collectLatest { result ->
                when (result) {
                    is AppResult.Success -> {
                        _uiState.value = ProfileUiState.Success(result.data)
                    }
                    is AppResult.Error -> {
                        _uiState.value = ProfileUiState.Error(result.error.message)
                    }
                }
            }
        }
    }

    fun retryUserPosts() {
        val userId = currentUserId ?: return
        observeFirstUserPostsPage(userId)
    }

    fun loadMoreUserPosts() {
        val userId = currentUserId ?: return
        val cursor = nextPostsCursor ?: return
        val currentState = _userPosts.value as? ProfilePostsUiState.Success ?: return

        if (currentState.isLoadingMore || !currentState.hasMore) {
            return
        }

        _userPosts.value = currentState.copy(
            isLoadingMore = true,
            loadMoreError = null
        )

        viewModelScope.launch {
            when (
                val result = postRepository.getUserPostsPage(
                    userId = userId,
                    pageSize = POSTS_PAGE_SIZE,
                    afterPostId = cursor
                )
            ) {
                is AppResult.Success -> {
                    olderUserPosts = mergePosts(
                        currentPosts = olderUserPosts,
                        newPosts = result.data.posts
                    )
                    nextPostsCursor = result.data.nextCursor
                    _userPosts.value = ProfilePostsUiState.Success(
                        posts = combineLoadedUserPosts(),
                        isLoadingMore = false,
                        hasMore = nextPostsCursor != null
                    )
                }

                is AppResult.Error -> {
                    Timber.e(result.error.throwable, "Failed to load more profile posts")
                    _userPosts.value = currentState.copy(
                        isLoadingMore = false,
                        loadMoreError = result.error.message
                    )
                }
            }
        }
    }

    private fun observeFirstUserPostsPage(userId: String) {
        postsFirstPageJob?.cancel()
        _userPosts.value = ProfilePostsUiState.Loading
        firstPageUserPosts = emptyList()
        olderUserPosts = emptyList()
        nextPostsCursor = null

        postsFirstPageJob = viewModelScope.launch {
            postRepository.getUserPosts(
                    userId = userId,
                    limit = (POSTS_PAGE_SIZE + 1).toLong()
                ).collectLatest { result ->
                    when (result) {
                        is AppResult.Success -> {
                            updateFirstUserPostsPage(result.data)
                        }

                        is AppResult.Error -> {
                            Timber.e(result.error.throwable, "Failed to load profile posts")
                            val currentState = _userPosts.value
                            if (currentState !is ProfilePostsUiState.Success) {
                                _userPosts.value = ProfilePostsUiState.Error(result.error.message)
                            }
                        }
                    }
                }
        }
    }

    private fun updateFirstUserPostsPage(posts: List<Post>) {
        val visiblePosts = posts.take(POSTS_PAGE_SIZE)

        if (olderUserPosts.isNotEmpty()) {
            val currentFirstPageIds = visiblePosts.mapTo(mutableSetOf()) { post ->
                post.id
            }
            val oldestVisiblePostTime = visiblePosts.minOfOrNull { post ->
                post.createdAt
            }
            val shiftedPosts = firstPageUserPosts.filter { post ->
                post.id !in currentFirstPageIds &&
                    oldestVisiblePostTime != null &&
                    post.createdAt <= oldestVisiblePostTime
            }
            olderUserPosts = mergePosts(shiftedPosts, olderUserPosts)
        }

        firstPageUserPosts = visiblePosts

        if (olderUserPosts.isEmpty()) {
            nextPostsCursor = if (posts.size > POSTS_PAGE_SIZE) {
                visiblePosts.lastOrNull()?.id
            } else {
                null
            }
        }

        _userPosts.value = ProfilePostsUiState.Success(
            posts = combineLoadedUserPosts(),
            isLoadingMore = false,
            hasMore = nextPostsCursor != null
        )
    }

    /**
     * Observa as runs locais do utilizador e calcula as estatísticas em tempo real.
     * Usa o Room via [RunRepository.getRunsByUser] — sem queries Firestore.
     */
    private fun loadProfileStats(userId: String) {
        viewModelScope.launch {
            runRepository.getRunStatistics(userId).collectLatest { statistics ->
                _profileStats.value = ProfileStats(
                    totalRuns = statistics.totalRuns,
                    totalKm = statistics.totalDistanceMeters / 1000.0,
                    totalMinutes = statistics.totalDurationSeconds / 60L
                )
            }
        }
    }

    private fun loadSavedRuns(userId: String) {
        savedRunsJob?.cancel()
        savedRunsJob = viewModelScope.launch {
            runRepository
                .getRecentCompletedRuns(userId, SAVED_RUNS_LIMIT)
                .collectLatest { runs ->
                    _savedRuns.value = runs
                }
        }
    }

    /** Atualiza o perfil do utilizador no Firestore. */
    fun updateUser(updatedUser: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = updateUserProfileUseCase(updatedUser)
            onComplete(result is AppResult.Success)
        }
    }

    /** Comprime e guarda a imagem de perfil através do repositório de utilizadores. */
    fun uploadImage(imageBytes: ByteArray, onComplete: (Boolean) -> Unit) {
        val userId = currentUserId
        if (userId == null) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            val result = uploadProfileImageUseCase(userId, imageBytes)
            onComplete(result is AppResult.Success)
        }
    }

    /** Termina a sessão e invoca o callback para navegar ao AuthActivity. */
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLogoutSuccess()
        }
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

    private fun combineLoadedUserPosts(): List<Post> {
        return mergePosts(firstPageUserPosts, olderUserPosts)
            .sortedByDescending { post -> post.createdAt }
    }

    private companion object {
        const val POSTS_PAGE_SIZE = 10
        const val SAVED_RUNS_LIMIT = 20
    }
}
