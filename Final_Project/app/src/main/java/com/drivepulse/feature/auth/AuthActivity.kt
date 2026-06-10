/**
 * Activity for authentication flows (Login, Register).
 * Replaced mock implementation with NavHost for Phase 2.
 *
 * Camada: UI
 * Feature: Auth
 */
package com.drivepulse.feature.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.drivepulse.core.common.Constants
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.designsystem.theme.DrivePulseTheme
import com.drivepulse.feature.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DrivePulseTheme {
                AuthNavGraph(
                    onNavigateToMain = { sessionMode ->
                        navigateToMain(sessionMode)
                    }
                )
            }
        }
    }

    private fun navigateToMain(sessionMode: SessionMode) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(Constants.EXTRA_SESSION_MODE, sessionMode.name)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
