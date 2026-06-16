package com.drivepulse.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.User
import com.drivepulse.domain.repository.AuthRepository
import com.drivepulse.domain.repository.PostRepository
import com.drivepulse.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsável por gerir o estado do ecrã Home.
 *
 * Camada: UI / Presentation
 * Feature: Home
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _recentPosts = MutableStateFlow<List<Post>>(emptyList())
    val recentPosts: StateFlow<List<Post>> = _recentPosts.asStateFlow()

    private var profileJob: Job? = null
    private var postsJob: Job? = null

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            authRepository.observeAuthState().collectLatest { user ->
                profileJob?.cancel()
                postsJob?.cancel()

                if (user != null) {
                    profileJob = launch {
                        userRepository.getUserProfile(user.id).collectLatest { result ->
                            if (result is AppResult.Success) {
                                _userProfile.value = result.data
                            } else {
                                _userProfile.value = user
                            }
                        }
                    }
                    postsJob = launch {
                        postRepository.getUserPosts(user.id).collectLatest { result ->
                            if (result is AppResult.Success) {
                                // Filter only posts with run data, take top 5
                                _recentPosts.value = result.data
                                    .filter { it.runId != null }
                                    .sortedByDescending { it.createdAt }
                                    .take(5)
                            }
                        }
                    }
                } else {
                    _userProfile.value = null
                    _recentPosts.value = emptyList()
                }
            }
        }
    }
}
