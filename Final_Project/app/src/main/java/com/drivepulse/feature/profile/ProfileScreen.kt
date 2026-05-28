package com.drivepulse.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.drivepulse.R
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpTextPrimary

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DpBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.title_profile), color = DpTextPrimary)
            DrivePulseButton(text = "Settings", onClick = onSettingsClick)
            DrivePulseButton(text = "Help", onClick = onHelpClick)
            DrivePulseButton(text = "About", onClick = onAboutClick)
            DrivePulseButton(text = "Premium", onClick = onPremiumClick)
            DrivePulseButton(
                text = "Logout", 
                onClick = { 
                    viewModel.logout(onLogoutSuccess = onLoginClick) 
                }
            )
        }
    }
}
