package com.drivepulse.feature.start

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drivepulse.R
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.components.DrivePulseOutlinedButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpSurface
import com.drivepulse.core.designsystem.theme.DpSurfaceVariant
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.core.designsystem.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun StartMenuScreen(
    onSignInClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    var contentVisible by remember { mutableStateOf(false) }
    var actionsVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0.72f,
        animationSpec = tween(
            durationMillis = 850,
            easing = FastOutSlowInEasing
        ),
        label = "startMenuLogoScale"
    )

    LaunchedEffect(Unit) {
        delay(120)
        contentVisible = true
        delay(450)
        actionsVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DpBackground, DpSurface)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            StartMenuHeader()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedStartMenuIdentity(
                    visible = contentVisible,
                    logoScale = logoScale
                )
            }

            AnimatedStartMenuActions(
                visible = actionsVisible,
                onSignInClick = onSignInClick,
                onGuestClick = onGuestClick
            )
        }
    }
}

@Composable
private fun AnimatedStartMenuIdentity(
    visible: Boolean,
    logoScale: Float
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 700)
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { fullHeight -> fullHeight / 4 }
        )
    ) {
        StartMenuIdentity(logoScale = logoScale)
    }
}

@Composable
private fun AnimatedStartMenuActions(
    visible: Boolean,
    onSignInClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 550)
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 550,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { fullHeight -> fullHeight / 2 }
        )
    ) {
        StartMenuActions(
            onSignInClick = onSignInClick,
            onGuestClick = onGuestClick
        )
    }
}

@Composable
private fun StartMenuHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.dp)
                .background(DpPrimaryRed)
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Text(
            text = stringResource(R.string.start_menu_eyebrow),
            color = DpTextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StartMenuIdentity(logoScale: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StartMenuLogo(
            modifier = Modifier.scale(logoScale)
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Text(
            text = stringResource(R.string.app_name),
            color = DpTextPrimary,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = stringResource(R.string.start_menu_tagline),
            color = DpTextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = stringResource(R.string.start_menu_highlight),
            color = DpPrimaryRed,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StartMenuLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .alpha(0.55f)
                .background(DpPrimaryRed)
        )

        Box(
            modifier = Modifier
                .size(126.dp)
                .background(
                    color = DpPrimaryRed.copy(alpha = 0.12f),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = DpPrimaryRed,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(
                        color = DpSurfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.DirectionsCar,
                    contentDescription = stringResource(R.string.cd_start_menu_logo),
                    modifier = Modifier.size(54.dp),
                    tint = DpPrimaryRed
                )
            }
        }
    }
}

@Composable
private fun StartMenuActions(
    onSignInClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        DrivePulseButton(
            text = stringResource(R.string.start_menu_sign_in),
            onClick = onSignInClick
        )

        DrivePulseOutlinedButton(
            text = stringResource(R.string.start_menu_guest),
            onClick = onGuestClick
        )
    }
}
