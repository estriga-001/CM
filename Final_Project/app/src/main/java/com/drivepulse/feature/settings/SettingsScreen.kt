/**
 * Ecrã de Definições da aplicação.
 *
 * Camada: UI
 * Feature: Settings
 *
 * Funcionalidades:
 * - Tema (Claro / Escuro / Sistema)
 * - Idioma (Português / English / Español)
 * - Alterar password
 * - Terminar sessão
 */
package com.drivepulse.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drivepulse.R
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpSurface
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.data.preferences.AppLanguage
import com.drivepulse.data.preferences.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = DpBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), color = DpTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = DpTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DpSurface)
            )
        }
    ) { paddingValues -> // rename padding to paddingValues to avoid any naming issues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Secção: Tema ---
            SettingsSection(
                icon = Icons.Filled.DarkMode,
                title = stringResource(R.string.settings_theme)
            ) {
                AppTheme.values().forEach { theme ->
                    val label = when (theme) {
                        AppTheme.LIGHT -> stringResource(R.string.settings_theme_light)
                        AppTheme.DARK -> stringResource(R.string.settings_theme_dark)
                        AppTheme.SYSTEM -> stringResource(R.string.settings_theme_system)
                    }
                    SettingsRadioOption(
                        label = label,
                        selected = currentTheme == theme,
                        onClick = { viewModel.setTheme(theme) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Secção: Idioma ---
            SettingsSection(
                icon = Icons.Filled.Language,
                title = stringResource(R.string.settings_language)
            ) {
                AppLanguage.values().forEach { language ->
                    SettingsRadioOption(
                        label = language.displayName,
                        selected = currentLanguage == language,
                        onClick = { viewModel.setLanguage(language) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Secção: Conta ---
            SettingsSection(
                icon = Icons.Filled.Lock,
                title = stringResource(R.string.settings_account)
            ) {
                val currentUserEmail = viewModel.currentUserEmail
                if (!currentUserEmail.isNullOrBlank()) {
                    Text(
                        text = stringResource(
                            R.string.settings_signed_in_as,
                            currentUserEmail
                        ),
                        color = DpTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Alterar password
                SettingsActionItem(
                    label = stringResource(R.string.settings_change_password),
                    subtitle = stringResource(R.string.settings_send_reset_email),
                    onClick = {
                        viewModel.sendPasswordReset { success, message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botão Logout ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DpCard, RoundedCornerShape(12.dp))
                    .clickable { viewModel.logout(onLogoutSuccess = onLogout) }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.settings_logout),
                        tint = DpPrimaryRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.settings_logout),
                        color = DpPrimaryRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Versão da app
            Text(
                text = "DrivePulse v1.0.0",
                color = DpTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Secção visual de settings com ícone, título e conteúdo.
 */
@Composable
private fun SettingsSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DpCard, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = DpPrimaryRed, modifier = Modifier.size(20.dp))
            Text(text = title, color = DpTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

/**
 * Opção de radio button para seleção de tema ou idioma.
 */
@Composable
private fun SettingsRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = DpPrimaryRed,
                unselectedColor = DpTextSecondary
            )
        )
        Text(text = label, color = DpTextPrimary, fontSize = 14.sp)
    }
}

/**
 * Item de ação clicável (ex: alterar password).
 */
@Composable
private fun SettingsActionItem(
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, color = DpTextPrimary, fontSize = 14.sp)
        if (subtitle != null) {
            Text(text = subtitle, color = DpTextSecondary, fontSize = 12.sp)
        }
    }
}
