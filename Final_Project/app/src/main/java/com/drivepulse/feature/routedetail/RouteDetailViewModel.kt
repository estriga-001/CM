package com.drivepulse.feature.routedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.AppResult
import com.drivepulse.domain.model.Post
import com.drivepulse.data.remote.dto.toDomain
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow<RouteDetailUiState>(RouteDetailUiState.Loading)
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()

    fun loadRouteDetail(postId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("posts").document(postId).get().await()
                if (doc.exists()) {
                    val postDto = doc.toObject(com.drivepulse.data.remote.dto.PostDto::class.java)
                    if (postDto != null) {
                        _uiState.value = RouteDetailUiState.Success(postDto.toDomain())
                    } else {
                        _uiState.value = RouteDetailUiState.Error("Dados da rota inválidos.")
                    }
                } else {
                    _uiState.value = RouteDetailUiState.Error("Rota não encontrada.")
                }
            } catch (e: Exception) {
                _uiState.value = RouteDetailUiState.Error(e.localizedMessage ?: "Falha ao carregar detalhes.")
            }
        }
    }
}

sealed interface RouteDetailUiState {
    object Loading : RouteDetailUiState
    data class Success(val post: Post) : RouteDetailUiState
    data class Error(val message: String) : RouteDetailUiState
}
