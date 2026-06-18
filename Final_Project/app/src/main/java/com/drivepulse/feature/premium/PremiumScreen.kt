package com.drivepulse.feature.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drivepulse.R
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.components.DrivePulseCard
import com.drivepulse.core.designsystem.components.DrivePulseElevatedCard
import com.drivepulse.core.designsystem.components.DrivePulseTopBar
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpDivider
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpSuccess
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.core.designsystem.theme.Spacing
import kotlinx.coroutines.launch

@Composable
fun PremiumScreen(
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isSubscriptionActive by remember { mutableStateOf(false) }
    val confirmationMessage = stringResource(R.string.premium_confirmation)

    Scaffold(
        topBar = {
            DrivePulseTopBar(
                title = stringResource(R.string.premium_title),
                onBackClick = onBackClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = DpBackground
    ) { innerPadding ->
        PremiumContent(
            isSubscriptionActive = isSubscriptionActive,
            onSubscribeClick = {
                isSubscriptionActive = true
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = confirmationMessage
                    )
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun PremiumContent(
    isSubscriptionActive: Boolean,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        PremiumHeader()
        PremiumPriceCard()
        PremiumBenefitsCard()

        if (isSubscriptionActive) {
            SubscriptionActiveCard()
        }

        DrivePulseButton(
            text = if (isSubscriptionActive) {
                stringResource(R.string.premium_subscription_active)
            } else {
                stringResource(R.string.premium_subscribe_button)
            },
            onClick = onSubscribeClick,
            enabled = !isSubscriptionActive
        )

        Text(
            text = stringResource(R.string.premium_simulation_notice),
            modifier = Modifier.fillMaxWidth(),
            color = DpTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.sm))
    }
}

@Composable
private fun PremiumHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = DpPrimaryRed.copy(alpha = 0.14f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = DpPrimaryRed
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = stringResource(R.string.premium_title),
            color = DpTextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = stringResource(R.string.premium_description),
            color = DpTextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PremiumPriceCard() {
    DrivePulseElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.premium_plan_label),
                color = DpPrimaryRed,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.premium_price),
                    color = DpTextPrimary,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = stringResource(R.string.premium_price_period),
                    modifier = Modifier.padding(bottom = 5.dp),
                    color = DpTextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = stringResource(R.string.premium_cancel_anytime),
                color = DpTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PremiumBenefitsCard() {
    val benefits = listOf(
        PremiumBenefit(
            icon = Icons.Rounded.Groups,
            title = stringResource(R.string.premium_benefit_groups_title),
            description = stringResource(R.string.premium_benefit_groups_description)
        ),
        PremiumBenefit(
            icon = Icons.Rounded.Speed,
            title = stringResource(R.string.premium_benefit_metrics_title),
            description = stringResource(R.string.premium_benefit_metrics_description)
        ),
        PremiumBenefit(
            icon = Icons.Rounded.History,
            title = stringResource(R.string.premium_benefit_history_title),
            description = stringResource(R.string.premium_benefit_history_description)
        ),
        PremiumBenefit(
            icon = Icons.Rounded.WorkspacePremium,
            title = stringResource(R.string.premium_benefit_badge_title),
            description = stringResource(R.string.premium_benefit_badge_description)
        )
    )

    Column {
        Text(
            text = stringResource(R.string.premium_benefits_title),
            color = DpTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        DrivePulseCard {
            benefits.forEachIndexed { index, benefit ->
                PremiumBenefitRow(benefit = benefit)

                if (index < benefits.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.md),
                        color = DpDivider
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumBenefitRow(benefit: PremiumBenefit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = DpPrimaryRed.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = benefit.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = DpPrimaryRed
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.md)
        ) {
            Text(
                text = benefit.title,
                color = DpTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(Spacing.xxxs))

            Text(
                text = benefit.description,
                color = DpTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SubscriptionActiveCard() {
    DrivePulseCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = DpSuccess
            )

            Text(
                text = stringResource(R.string.premium_active_message),
                modifier = Modifier.padding(start = Spacing.md),
                color = DpTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private data class PremiumBenefit(
    val icon: ImageVector,
    val title: String,
    val description: String
)
