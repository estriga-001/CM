/**
 * ViewModel para o ecrã de Definições.
 *
 * Camada: Presentation
 * Feature: Settings
 *
 * Responsabilidades:
 * - Ler e expor o tema e idioma actuais via StateFlow.
 * - Permitir ao utilizador alterar tema e idioma.
 * - Aplicar o idioma via AppCompatDelegate.
 * - Permitir logout e reenvio de password.
 */
package com.drivepulse.feature.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.data.preferences.AppLanguage
import com.drivepulse.data.preferences.AppTheme
import com.drivepulse.data.preferences.PreferencesManager
import com.drivepulse.domain.usecase.auth.LogoutUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val logoutUseCase: LogoutUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val currentUserEmail: String?
        get() = firebaseAuth.currentUser?.email

    /** Tema actual persistido no DataStore. */
    val currentTheme: StateFlow<AppTheme> = preferencesManager.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppTheme.SYSTEM)

    /** Idioma actual persistido no DataStore. */
    val currentLanguage: StateFlow<AppLanguage> = preferencesManager.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.PT)

    /** Altera o tema e aplica via AppCompatDelegate. */
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.setTheme(theme)
            applyTheme(theme)
        }
    }

    /** Altera o idioma e aplica via AppCompatDelegate. */
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
            applyLanguage(language)
        }
    }

    /** Envia email de redefinição de password para o email do utilizador actual. */
    fun sendPasswordReset(onResult: (Boolean, String) -> Unit) {
        val email = firebaseAuth.currentUser?.email
        if (email.isNullOrBlank()) {
            onResult(false, "Sem email associado à conta.")
            return
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Timber.d("✅ Email de redefinição de password enviado para $email")
                onResult(true, "Email de redefinição enviado para $email")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "❌ Erro ao enviar email de redefinição")
                onResult(false, e.localizedMessage ?: "Erro ao enviar email.")
            }
    }

    /** Termina a sessão do utilizador. */
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLogoutSuccess()
        }
    }

    /** Aplica o modo de tema via AppCompatDelegate. */
    private fun applyTheme(theme: AppTheme) {
        val mode = when (theme) {
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /** Aplica o idioma via AppCompatDelegate (API 33+). */
    private fun applyLanguage(language: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
