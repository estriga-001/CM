/**
 * Bottom navigation item data class.
 *
 * Camada: Core / Navigation
 * Feature: Navigation
 */
package com.drivepulse.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.drivepulse.R

/**
 * Represents a single bottom navigation tab.
 *
 * @property route the navigation route string.
 * @property labelResId string resource for the label.
 * @property selectedIcon filled icon when selected.
 * @property unselectedIcon outlined icon when not selected.
 */
data class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    companion object {
        /**
         * The four main bottom nav items (Run is handled separately as a FAB).
         */
        val items = listOf(
            BottomNavItem(
                route = AppDestination.HOME,
                labelResId = R.string.nav_home,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            BottomNavItem(
                route = AppDestination.MAP,
                labelResId = R.string.nav_map,
                selectedIcon = Icons.Filled.Map,
                unselectedIcon = Icons.Outlined.Map
            ),
            // Run button is handled separately in the BottomBar composable
            BottomNavItem(
                route = AppDestination.COMMUNITY,
                labelResId = R.string.nav_community,
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore
            ),
            BottomNavItem(
                route = AppDestination.PROFILE,
                labelResId = R.string.nav_profile,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            )
        )
    }
}
