package com.drivepulse.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.drivepulse.R
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpTextPrimary

@Composable
fun HomeScreen(
    onStartRun: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DpBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.title_home), color = DpTextPrimary)
    }
}
