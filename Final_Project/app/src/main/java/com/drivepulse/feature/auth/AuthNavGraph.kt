/**
 * Navigation routes and graph for Authentication flow.
 *
 * Camada: UI
 * Feature: Auth
 */
package com.drivepulse.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepulse.core.common.SessionMode
import com.drivepulse.feature.auth.screens.LoginScreen
import com.drivepulse.feature.auth.screens.RegisterScreen
import com.drivepulse.feature.profile.ProfileSetupScreen
import com.drivepulse.feature.profile.ProfileSetupViewModel

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
    NavHost(
        navController = navController,
        startDestination = AuthDestination.LOGIN
    ) {
        composable(AuthDestination.LOGIN) {
            LoginScreen(
                onNavigateToMain = onNavigateToMain,
                onNavigateToRegister = {
                    navController.navigate(AuthDestination.REGISTER)
                }
            )
        }
        
        composable(AuthDestination.REGISTER) {
            RegisterScreen(
                onNavigateToMain = {
                    // Após registo normal, força ida para Setup
                    if (it == SessionMode.AUTHENTICATED) {
                        navController.navigate(AuthDestination.SETUP)
                    } else {
                        onNavigateToMain(it)
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(AuthDestination.SETUP) {
            val viewModel: ProfileSetupViewModel = hiltViewModel()
            val isComplete by viewModel.isComplete.collectAsStateWithLifecycle()
            
            if (isComplete) {
                onNavigateToMain(SessionMode.AUTHENTICATED)
            }
            
            ProfileSetupScreen(
                onSetupComplete = { brand, model, year ->
                    viewModel.saveCarProfile(brand, model, year)
                },
                onSkip = {
                    onNavigateToMain(SessionMode.AUTHENTICATED)
                }
            )
        }
    }
}
