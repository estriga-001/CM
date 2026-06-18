/**
 * Main navigation graph for the MainActivity.
 * Defines all routes and screen composables within the main scaffold.
 *
 * Camada: Core / Navigation
 * Feature: Navigation
 */
package com.drivepulse.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.drivepulse.feature.about.AboutScreen
import com.drivepulse.feature.community.CommunityViewModel
import com.drivepulse.feature.community.screens.CommunityScreen
import com.drivepulse.feature.createpost.CreatePostScreen
import com.drivepulse.feature.help.HelpScreen
import com.drivepulse.feature.home.HomeRoute
import com.drivepulse.feature.map.MapScreen
import com.drivepulse.feature.premium.PremiumScreen
import com.drivepulse.feature.profile.EditProfileScreen
import com.drivepulse.feature.profile.ProfileScreen
import com.drivepulse.feature.settings.SettingsScreen

/**
 * NavHost for the main app navigation within MainActivity.
 *
 * @param navController the navigation controller.
 * @param onStartRun callback to launch RunRecorderActivity.
 * @param onNavigateToAuth callback to launch AuthActivity.
 * @param modifier optional Modifier.
 */
@Composable
fun MainNavGraph(
    navController: NavHostController,
    onStartRun: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onOpenRouteDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.HOME,
        modifier = modifier
    ) {
        composable(AppDestination.HOME) {
            HomeRoute(
                onStartRun = onStartRun,
                onNavigateToMap = { 
                    navController.navigate(AppDestination.MAP) {
                        popUpTo(AppDestination.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToCommunity = { 
                    navController.navigate(AppDestination.COMMUNITY) {
                        popUpTo(AppDestination.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToPremium = { navController.navigate(AppDestination.PREMIUM) }
            )
        }

        composable(AppDestination.MAP) {
            com.drivepulse.feature.map.MapRoute(
                onPinClick = onOpenRouteDetail
            )
        }

        composable(AppDestination.COMMUNITY) {
            val viewModel: CommunityViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            CommunityScreen(
                uiState = uiState,
                viewModel = viewModel,
                onPostClick = onOpenRouteDetail
            )
        }

        composable(AppDestination.PROFILE) {
            ProfileScreen(
                onEditProfileClick = {
                    navController.navigate(AppDestination.EDIT_PROFILE)
                },
                onSettingsClick = {
                    navController.navigate(AppDestination.SETTINGS)
                },
                onHelpClick = {
                    navController.navigate(AppDestination.HELP)
                },
                onAboutClick = {
                    navController.navigate(AppDestination.ABOUT)
                },
                onPremiumClick = {
                    navController.navigate(AppDestination.PREMIUM)
                },
                onLoginClick = onNavigateToAuth,
                onPostClick = onOpenRouteDetail
            )
        }

        composable(AppDestination.EDIT_PROFILE) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = onNavigateToAuth
            )
        }

        composable(AppDestination.HELP) {
            HelpScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.PREMIUM) {
            PremiumScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.CREATE_POST) { backStackEntry ->
            val runId = backStackEntry.arguments?.getString("runId") ?: ""
            CreatePostScreen(
                runId = runId,
                onBackClick = { navController.popBackStack() },
                onPublished = { navController.popBackStack() }
            )
        }
    }
}
