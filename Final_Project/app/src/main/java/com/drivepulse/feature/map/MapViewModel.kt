package com.drivepulse.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _feedPosts = MutableStateFlow<List<Post>>(emptyList())
    val feedPosts: StateFlow<List<Post>> = _feedPosts.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            postRepository.getFeedPosts().collectLatest { result ->
                if (result is AppResult.Success) {
                    _feedPosts.value = result.data
                }
            }
        }
    }
}
