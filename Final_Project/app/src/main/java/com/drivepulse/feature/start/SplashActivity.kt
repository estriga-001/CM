package com.drivepulse.feature.start

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.drivepulse.core.common.Constants
import com.drivepulse.core.common.SessionMode
import com.drivepulse.core.designsystem.theme.DrivePulseTheme
import com.drivepulse.feature.auth.AuthActivity
import com.drivepulse.feature.main.MainActivity

/**
 * Entry point that lets the user choose how to start DrivePulse.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrivePulseTheme(darkTheme = true) {
                StartMenuScreen(
                    onSignInClick = ::openAuthentication,
                    onGuestClick = ::openGuestSession
                )
            }
        }
    }

    private fun openAuthentication() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

    private fun openGuestSession() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(Constants.EXTRA_SESSION_MODE, SessionMode.GUEST.name)
        }
        startActivity(intent)
    }
}
