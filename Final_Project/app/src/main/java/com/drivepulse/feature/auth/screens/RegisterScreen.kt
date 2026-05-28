/**
 * Register Screen composable.
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepulse.R
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpDanger
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.core.designsystem.theme.Spacing
import com.drivepulse.feature.auth.AuthViewModel
import com.drivepulse.feature.auth.state.AuthState

@Composable
fun RegisterScreen(
    onNavigateToMain: (SessionMode) -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthState.Success) {
            onNavigateToMain(SessionMode.AUTHENTICATED)
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
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.headlineLarge,
                color = DpTextPrimary,
                fontWeight = FontWeight.Bold
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
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
            
            Spacer(modifier = Modifier.height(Spacing.xl))

            if (uiState is AuthState.Loading) {
                CircularProgressIndicator(color = DpPrimaryRed)
            } else {
                DrivePulseButton(
                    text = stringResource(R.string.register),
                    onClick = { viewModel.register(email, password, confirmPassword) }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
            
            Text(
                text = "Already have an account? Login",
                color = DpTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { 
                    viewModel.resetState()
                    onBackToLogin() 
                }
            )
        }
    }
}
