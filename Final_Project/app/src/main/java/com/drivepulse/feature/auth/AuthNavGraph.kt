/**
 * Navigation routes and graph for Authentication flow.
 *
 * Camada: UI
 * Feature: Auth
 */
package com.drivepulse.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.feature.auth.screens.LoginScreen
import com.drivepulse.feature.auth.screens.RegisterScreen
import com.drivepulse.feature.auth.state.AuthState
import com.drivepulse.feature.profile.ProfileSetupScreen
import com.drivepulse.feature.profile.ProfileSetupViewModel
import com.drivepulse.feature.profile.OnboardingUiState

object AuthDestination {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val SETUP = "setup"
}

@Composable
fun AuthNavGraph(
    onNavigateToMain: (SessionMode) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    // Shared AuthViewModel — lives at this NavGraph level so both LOGIN and REGISTER share it.
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Handle session restore at the top level BEFORE the NavHost renders anything.
    // SessionRestored means the user was already logged in — bypass the login UI.
    LaunchedEffect(uiState) {
        if (uiState is AuthState.SessionRestored) {
            val user = (uiState as AuthState.SessionRestored).user
            if (user.username.isNotEmpty()) {
                onNavigateToMain(SessionMode.AUTHENTICATED)
            } else {
                navController.navigate(AuthDestination.SETUP) {
                    popUpTo(AuthDestination.LOGIN) { inclusive = true }
                }
            }
        }
    }

    // Show a full-screen loading splash while the session check is in progress.
    if (uiState is AuthState.Loading) {
        Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .background(DpBackground),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(color = DpPrimaryRed)
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = AuthDestination.LOGIN
    ) {
        composable(AuthDestination.LOGIN) {
            LoginScreen(
                onNavigateToMain = onNavigateToMain,
                onNavigateToRegister = {
                    navController.navigate(AuthDestination.REGISTER)
                },
                onNavigateToSetup = {
                    navController.navigate(AuthDestination.SETUP) {
                        popUpTo(AuthDestination.LOGIN) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(AuthDestination.REGISTER) {
            RegisterScreen(
                onNavigateToMain = {
                    // After registration, always go to Setup to fill profile.
                    if (it == SessionMode.AUTHENTICATED) {
                        navController.navigate(AuthDestination.SETUP) {
                            popUpTo(AuthDestination.LOGIN) { inclusive = true }
                        }
                    } else {
                        onNavigateToMain(it)
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable(AuthDestination.SETUP) {
            val setupViewModel: ProfileSetupViewModel = hiltViewModel()
            val setupUiState by setupViewModel.uiState.collectAsStateWithLifecycle()
            val usernameState by setupViewModel.usernameState.collectAsStateWithLifecycle()

            LaunchedEffect(setupUiState) {
                if (setupUiState is OnboardingUiState.Success) {
                    onNavigateToMain(SessionMode.AUTHENTICATED)
                }
            }

            ProfileSetupScreen(
                uiState = setupUiState,
                usernameState = usernameState,
                onCheckUsername = { setupViewModel.checkUsernameAvailability(it) },
                onSubmit = { username, firstName, lastName, brand, model, year ->
                    setupViewModel.submitOnboarding(username, firstName, lastName, brand, model, year)
                },
                onCancel = {
                    // User pressed back/cancel — sign out and go back to Login
                    setupViewModel.logout {
                        navController.navigate(AuthDestination.LOGIN) {
                            popUpTo(AuthDestination.SETUP) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

