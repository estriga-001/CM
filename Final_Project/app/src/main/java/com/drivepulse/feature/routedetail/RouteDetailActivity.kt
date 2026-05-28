/**
 * Activity to show the details of a specific route/post.
 *
 * Camada: UI
 * Feature: Route Detail
 */
package com.drivepulse.feature.routedetail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drivepulse.R
import com.drivepulse.core.common.Constants
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.components.DrivePulseTopBar
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DrivePulseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val routeId = intent.getStringExtra(Constants.EXTRA_ROUTE_ID) ?: "Unknown Route"
        
        setContent {
            DrivePulseTheme {
                Scaffold(
                    topBar = {
                        DrivePulseTopBar(
                            title = stringResource(R.string.title_route_detail),
                            onBackClick = { finish() }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DpBackground)
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Route Detail for: $routeId",
                            color = DpTextPrimary
                        )
                        
                        DrivePulseButton(
                            text = "Save Route & Return",
                            onClick = {
                                val resultIntent = Intent().apply {
                                    putExtra(Constants.EXTRA_ROUTE_SAVED, true)
                                }
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
