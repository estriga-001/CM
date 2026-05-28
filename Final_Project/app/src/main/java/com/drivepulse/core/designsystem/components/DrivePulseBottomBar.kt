/**
 * Bottom navigation bar for DrivePulse with 5 tabs.
 * The central Run button is visually highlighted.
 *
 * Camada: Core / Design System
 * Feature: Components
 */
package com.drivepulse.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drivepulse.R
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpDivider
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextMuted
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.Spacing
import com.drivepulse.core.navigation.BottomNavItem

/**
 * Main bottom navigation bar with 5 tabs.
 * The central "+ Run" button is elevated and colored red.
 *
 * @param items list of navigation items.
 * @param selectedRoute the currently selected route string.
 * @param onItemClick callback when a nav item is tapped.
 * @param onRunClick callback for the central Run button.
 */
@Composable
fun DrivePulseBottomBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    onRunClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DpBackground)
    ) {
        // Thin divider line at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(DpDivider)
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First two items (Home, Map)
            items.take(2).forEach { item ->
                BottomNavTab(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Central Run button
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = (-12).dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DpPrimaryRed)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onRunClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.nav_run),
                        tint = DpTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Last two items (Community, Profile)
            items.drop(2).forEach { item ->
                BottomNavTab(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Individual bottom navigation tab with icon and label.
 */
@Composable
private fun BottomNavTab(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isSelected) DpPrimaryRed else DpTextMuted,
        label = "navTabColor"
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = Spacing.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = stringResource(item.labelResId),
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(item.labelResId),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
