/**
 * Login Screen composable.
 *
 * Camada: UI
 * Feature: Auth
 */
package com.drivepulse.feature.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.drivepulse.R
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.components.DrivePulseOutlinedButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpDanger
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.core.designsystem.theme.Spacing
import com.drivepulse.feature.auth.AuthViewModel
import com.drivepulse.feature.auth.state.AuthState

@Composable
fun LoginScreen(
    onNavigateToMain: (SessionMode) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToSetup: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var googleError by remember { mutableStateOf<String?>(null) }
    var isGoogleLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthState.Success -> {
                // Manual login just completed — check if onboarding is done.
                if (state.user.username.isNotEmpty()) {
                    onNavigateToMain(SessionMode.AUTHENTICATED)
                } else {
                    onNavigateToSetup()
                }
            }
            // SessionRestored is handled in AuthNavGraph before this screen renders.
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DpBackground)
            .padding(Spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium,
                color = DpPrimaryRed,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxl))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = DpTextSecondary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                ),
                singleLine = true
            )

            if (uiState is AuthState.Error) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = (uiState as AuthState.Error).message,
                    color = DpDanger,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            if (googleError != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = googleError!!,
                    color = DpDanger,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))

            if (uiState is AuthState.Loading) {
                CircularProgressIndicator(color = DpPrimaryRed)
            } else {
                DrivePulseButton(
                    text = stringResource(R.string.login),
                    onClick = { viewModel.login(email, password) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
            
            Text(
                text = "Don't have an account? Register",
                color = DpTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { 
                    viewModel.resetState()
                    onNavigateToRegister() 
                }
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))
            
            Text(text = "OU", color = DpTextSecondary, style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            if (isGoogleLoading) {
                CircularProgressIndicator(color = DpPrimaryRed)
            } else {
                DrivePulseOutlinedButton(
                    text = "Continuar com Google",
                    onClick = {
                        coroutineScope.launch {
                            googleError = null
                            isGoogleLoading = true
                            try {
                                val credentialManager = CredentialManager.create(context)
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setAutoSelectEnabled(false)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(context, request)
                                val credential = result.credential
                                if (credential is CustomCredential &&
                                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential =
                                        GoogleIdTokenCredential.createFrom(credential.data)
                                    viewModel.googleSignIn(googleIdTokenCredential.idToken)
                                } else {
                                    googleError = "Credencial Google inválida. Tenta novamente."
                                }
                            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                                // Utilizador cancelou — não mostrar erro
                            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                                googleError = "Nenhuma conta Google encontrada neste dispositivo. Adiciona uma conta nas Definições do Android."
                            } catch (e: Exception) {
                                googleError = "Erro Google Sign-In: ${e.localizedMessage ?: "Verifica a ligação à internet."}."
                            } finally {
                                isGoogleLoading = false
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))
            
            DrivePulseOutlinedButton(
                text = stringResource(R.string.continue_as_guest),
                onClick = { onNavigateToMain(SessionMode.GUEST) }
            )
        }
    }
}
