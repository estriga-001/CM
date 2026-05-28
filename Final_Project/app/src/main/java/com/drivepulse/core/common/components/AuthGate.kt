package com.drivepulse.core.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.drivepulse.core.common.LocalSessionMode
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary

/**
 * AuthGate is a headless component that provides an interceptor function.
 * If the user is a GUEST, the interceptor blocks the action and shows an AlertDialog
 * prompting them to create an account or log in.
 *
 * Usage:
 * ```
 * AuthGate(onNavigateToAuth = { navigateToLogin() }) { withAuth ->
 *     Button(onClick = withAuth { performSecureAction() }) {
 *         Text("Secure Action")
 *     }
 * }
 * ```
 */
@Composable
fun AuthGate(
    onNavigateToAuth: () -> Unit,
    content: @Composable (withAuth: (() -> Unit) -> () -> Unit) -> Unit
) {
    val sessionMode = LocalSessionMode.current
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Acesso Restrito", color = DpTextPrimary)
            },
            text = {
                Text(
                    text = "Para aceder a esta funcionalidade (como iniciar runs ou publicar), precisas de criar uma conta gratuita ou fazer login.",
                    color = DpTextPrimary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onNavigateToAuth()
                }) {
                    Text("Fazer Login / Registo", color = DpPrimaryRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = DpTextPrimary)
                }
            },
            containerColor = com.drivepulse.core.designsystem.theme.DpSurface,
            titleContentColor = DpTextPrimary,
            textContentColor = DpTextPrimary
        )
    }

    val withAuth: (() -> Unit) -> () -> Unit = { action ->
        {
            if (sessionMode == SessionMode.GUEST) {
                showDialog = true
            } else {
                action()
            }
        }
    }

    content(withAuth)
}
