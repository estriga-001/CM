/**
 * Main Activity holding the Bottom Navigation and NavHost.
 *
 * Camada: UI
 * Feature: Main
 */
package com.drivepulse.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
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
import com.drivepulse.data.preferences.AppTheme
import com.drivepulse.feature.auth.AuthActivity
import com.drivepulse.feature.run.RunRecorderActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    /** Guarda referência ao NavController para navegação pós-result. */
    private var navControllerRef: androidx.navigation.NavHostController? = null

    /**
     * Launcher que recebe o resultado da RunRecorderActivity.
     * Se o utilizador carregou em "Publicar", recebemos o runId
     * e navegamos para o CreatePostScreen.
     */
    private val runActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val runId = result.data?.getStringExtra(Constants.EXTRA_RUN_ID) ?: ""
            if (runId.isNotBlank()) {
                navControllerRef?.navigate(AppDestination.createPostRoute(runId))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionModeString = intent.getStringExtra(Constants.EXTRA_SESSION_MODE) ?: SessionMode.GUEST.name
        val sessionMode = SessionMode.valueOf(sessionModeString)

        setContent {
            // Observe theme preference from DataStore and resolve it to a Boolean
            val appTheme by mainViewModel.appTheme.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (appTheme) {
                AppTheme.DARK   -> true
                AppTheme.LIGHT  -> false
                AppTheme.SYSTEM -> systemDark
            }

            DrivePulseTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                navControllerRef = navController
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
        runActivityLauncher.launch(intent)
    }

    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
