/**
 * Gestor de preferências de utilizador usando AndroidX DataStore.
 *
 * Camada: Data
 * Feature: Settings
 *
 * Guarda e expõe:
 * - Tema (claro/escuro/sistema)
 * - Idioma (pt, en, es)
 */
package com.drivepulse.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Extensão para aceder ao DataStore de preferências. */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drivepulse_prefs")

/**
 * Valores possíveis para o tema da aplicação.
 */
enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Valores possíveis para o idioma da aplicação.
 */
enum class AppLanguage(val code: String, val displayName: String) {
    PT("pt", "Português"),
    EN("en", "English"),
    ES("es", "Español")
}

/**
 * Gestor de preferências persistentes via DataStore.
 * Injetado como Singleton via Hilt.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_THEME = stringPreferencesKey("app_theme")
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
    }

    /** Observa o tema actual como Flow. */
    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        try {
            AppTheme.valueOf(prefs[KEY_THEME] ?: AppTheme.SYSTEM.name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    /** Observa o idioma actual como Flow. */
    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        try {
            AppLanguage.valueOf(prefs[KEY_LANGUAGE] ?: AppLanguage.PT.name)
        } catch (e: Exception) {
            AppLanguage.PT
        }
    }

    /** Persiste o tema escolhido. */
    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = theme.name
        }
    }

    /** Persiste o idioma escolhido. */
    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.name
        }
    }
}
