package de.hsb.greenquest.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import de.hsb.greenquest.R

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val icon: Painter,
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavigationItem(
            title = Screen.CameraScreen.title.asString(),
            route = Screen.CameraScreen.route,
            icon = painterResource(id = R.drawable.kamera)
        ),
        BottomNavigationItem(
            title = Screen.PortfolioScreen.title.asString(),
            route = Screen.PortfolioScreen.route,
            icon = painterResource(id = R.drawable.photo_album)
        ),
        BottomNavigationItem(
            title = Screen.ChallengeScreen.title.asString(),
            route = Screen.ChallengeScreen.route,
            icon = painterResource(id = R.drawable.flash_card)
        ),

        BottomNavigationItem(
            title = Screen.NearbyShareScreen.title.asString(),
            route = Screen.NearbyShareScreen.route,
            icon = painterResource(id = R.drawable.flash_card)
        ),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                colors = NavigationBarItemColors(
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIconColor = MaterialTheme.colorScheme.onPrimary,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.background
                ),
                selected = isSelected,
                onClick = {
                    navController.popBackStack(route = item.route, inclusive = false)
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Image(item.icon, contentDescription = item.title)
                },
                label = {
                    Text(text = item.title)
                }
            )
        }
    }
}