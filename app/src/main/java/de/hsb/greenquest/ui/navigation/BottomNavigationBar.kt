package de.hsb.greenquest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavigationItem(
            title = "Camera",
            route = Screen.CameraScreen.route,
            icon = Icons.Filled.Delete
        ),
        BottomNavigationItem(
            title = "Portfolio",
            route = Screen.PortfolioScreen.route,
            icon = Icons.Filled.Notifications
        ),
    )
    var selectedItemIndex by rememberSaveable() {
        mutableStateOf(0)
    }
    NavigationBar {
        items.forEachIndexed { index, bottomNavItem->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate(bottomNavItem.route)
                },
                icon = {
                    Icon(bottomNavItem.icon, contentDescription = "")
                },
                label = {
                    Text(text = bottomNavItem.title)
                }
            )
        }
    }
}