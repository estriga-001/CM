/**
 * AuthGate composable that blocks guest users from performing protected actions.
 * Shows a dialog prompting the user to log in or register.
 *
 * Camada: Core / Design System
 * Feature: Components / Auth
 */
package com.drivepulse.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.drivepulse.R
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary

/**
 * Dialog shown to guest users when they attempt a protected action.
 * Offers options to go to login or dismiss.
 *
 * @param onLoginClick callback to navigate to login.
 * @param onDismiss callback to dismiss the dialog.
 */
@Composable
fun AuthRequiredDialog(
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DpCard,
        title = {
            Text(
                text = stringResource(R.string.auth_required_title),
                style = MaterialTheme.typography.titleLarge,
                color = DpTextPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.auth_required_message),
                style = MaterialTheme.typography.bodyMedium,
                color = DpTextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onLoginClick) {
                Text(
                    text = stringResource(R.string.login),
                    color = DpPrimaryRed
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = DpTextSecondary
                )
            }
        }
    )
}
