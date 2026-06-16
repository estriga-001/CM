package com.drivepulse.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepulse.R
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.components.DrivePulseOutlinedButton
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.domain.model.Post
import com.drivepulse.domain.model.User

@Composable
fun HomeRoute(
    onStartRun: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToPremium: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val recentPosts by viewModel.recentPosts.collectAsStateWithLifecycle()

    HomeScreen(
        userProfile = userProfile,
        recentPosts = recentPosts,
        onStartRun = onStartRun,
        onNavigateToMap = onNavigateToMap,
        onNavigateToCommunity = onNavigateToCommunity,
        onNavigateToPremium = onNavigateToPremium
    )
}

@Composable
fun HomeScreen(
    userProfile: User?,
    recentPosts: List<Post>,
    onStartRun: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToPremium: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // App Logo/Title
        Text(
            text = stringResource(R.string.home_title_logo),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DpTextPrimary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Greeting
        if (userProfile?.username?.isNotEmpty() == true) {
            Text(
                text = stringResource(R.string.home_greeting, userProfile.username),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DpTextPrimary
            )
        } else {
            Text(
                text = stringResource(R.string.home_greeting_fallback),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DpTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Last Run Info
        val lastRun = recentPosts.firstOrNull()
        if (lastRun != null) {
            val distanceKm = lastRun.distanceMeters / 1000f
            val durationMin = lastRun.durationSeconds / 60
            Text(
                text = stringResource(R.string.home_last_run, distanceKm, durationMin),
                style = MaterialTheme.typography.bodyMedium,
                color = DpTextSecondary
            )
        } else {
            Text(
                text = stringResource(R.string.home_no_recent_runs),
                style = MaterialTheme.typography.bodyMedium,
                color = DpTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Big Action Button
        DrivePulseButton(
            text = stringResource(R.string.home_start_run),
            onClick = onStartRun
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                DrivePulseOutlinedButton(
                    text = stringResource(R.string.home_shortcut_map),
                    onClick = onNavigateToMap
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                DrivePulseOutlinedButton(
                    text = stringResource(R.string.home_shortcut_community),
                    onClick = onNavigateToCommunity
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // Premium Banner
        PremiumBanner(onClick = onNavigateToPremium)

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Runs Section
        Text(
            text = stringResource(R.string.home_recent_runs),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = DpTextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (recentPosts.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recentPosts) { post ->
                    MiniRunCard(post = post)
                }
            }
        }
    }
}

@Composable
fun PremiumBanner(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.home_premium_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = com.drivepulse.core.designsystem.theme.DpPrimaryRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.home_premium_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = DpTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_premium_btn) + " →",
                style = MaterialTheme.typography.labelLarge,
                color = com.drivepulse.core.designsystem.theme.DpPrimaryRed
            )
        }
    }
}

@Composable
fun MiniRunCard(post: Post) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = post.description.ifEmpty { "Run" },
            style = MaterialTheme.typography.titleSmall,
            color = DpTextPrimary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        val distanceKm = post.distanceMeters / 1000f
        Text(
            text = String.format("%.1f km", distanceKm),
            style = MaterialTheme.typography.bodyMedium,
            color = DpTextSecondary
        )
    }
}
