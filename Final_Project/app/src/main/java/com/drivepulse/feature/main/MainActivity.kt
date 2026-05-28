/**
 * Main Activity holding the Bottom Navigation and NavHost.
 *
 * Camada: UI
 * Feature: Main
 */
package com.drivepulse.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.drivepulse.core.common.Constants
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.common.LocalSessionMode
import com.drivepulse.core.common.components.AuthGate
import com.drivepulse.core.designsystem.components.DrivePulseBottomBar
import com.drivepulse.core.designsystem.theme.DrivePulseTheme
import com.drivepulse.core.navigation.AppDestination
import com.drivepulse.core.navigation.BottomNavItem
import com.drivepulse.core.navigation.MainNavGraph
import androidx.compose.runtime.CompositionLocalProvider
import com.drivepulse.feature.auth.AuthActivity
import com.drivepulse.feature.run.RunRecorderActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionModeString = intent.getStringExtra(Constants.EXTRA_SESSION_MODE) ?: SessionMode.GUEST.name
        val sessionMode = SessionMode.valueOf(sessionModeString)

        setContent {
            DrivePulseTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: AppDestination.HOME
                
                // Show bottom bar only on the 4 main tabs
                val showBottomBar = BottomNavItem.items.any { it.route == currentRoute }

                CompositionLocalProvider(LocalSessionMode provides sessionMode) {
                    AuthGate(onNavigateToAuth = { navigateToAuth() }) { withAuth ->
                        Scaffold(
                            bottomBar = {
                                if (showBottomBar) {
                                    DrivePulseBottomBar(
                                        items = BottomNavItem.items,
                                        selectedRoute = currentRoute,
                                        onItemClick = { item ->
                                            navController.navigate(item.route) {
                                                popUpTo(AppDestination.HOME) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        onRunClick = withAuth {
                                            startRunActivity()
                                        }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            MainNavGraph(
                                navController = navController,
                                onStartRun = withAuth { startRunActivity() },
                                onNavigateToAuth = { navigateToAuth() },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startRunActivity() {
        val intent = Intent(this, RunRecorderActivity::class.java).apply {
            putExtra(Constants.EXTRA_PRIVACY_MODE, "PRIVATE")
        }
        startActivity(intent)
    }

    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
