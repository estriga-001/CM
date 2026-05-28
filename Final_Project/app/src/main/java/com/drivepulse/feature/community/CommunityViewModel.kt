package com.drivepulse.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.domain.usecase.run.GetCommunityRunsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    getCommunityRunsUseCase: GetCommunityRunsUseCase
) : ViewModel() {

    val uiState: StateFlow<CommunityUiState> = getCommunityRunsUseCase()
        .map { runs ->
            CommunityUiState.Success(runs) as CommunityUiState
        }
        .catch { e ->
            Timber.e(e, "Erro ao carregar o feed da comunidade")
            emit(CommunityUiState.Error("Não foi possível carregar as publicações. Verifica a tua ligação à internet."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommunityUiState.Loading
        )
}
