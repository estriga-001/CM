/**
 * ViewModel for MainActivity.
 *
 * Camada: Presentation
 * Feature: Main
 *
 * Responsabilidades:
 * - Expor o tema actual do DataStore para que a MainActivity
 *   aplique o DrivePulseTheme correcto antes de desenhar qualquer UI.
 */
package com.drivepulse.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.data.preferences.AppTheme
import com.drivepulse.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * Tema actual lido do DataStore.
     * O valor inicial é SYSTEM até o Flow emitir o valor persistido.
     */
    val appTheme: StateFlow<AppTheme> = preferencesManager.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppTheme.SYSTEM
        )

    /**
     * Idioma actual lido do DataStore.
     */
    val appLanguage: StateFlow<com.drivepulse.data.preferences.AppLanguage> = preferencesManager.languageFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = com.drivepulse.data.preferences.AppLanguage.PT
        )
}
