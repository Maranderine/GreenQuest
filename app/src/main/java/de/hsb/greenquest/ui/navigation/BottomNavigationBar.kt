package de.hsb.greenquest.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
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
            title = "Camera",
            route = Screen.CameraScreen.route,
            icon = painterResource(id = R.drawable.kamera)
        ),
        BottomNavigationItem(
            title = "Portfolio",
            route = Screen.PortfolioScreen.route,
            icon = painterResource(id = R.drawable.photo_album)
        ),
        BottomNavigationItem(
            title = "Challenges",
            route = Screen.ChallengeScreen.route,
            icon = painterResource(id = R.drawable.flash_card)
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

                    navController.navigate(bottomNavItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Image(bottomNavItem.icon, contentDescription = "")
                },
                label = {
                    Text(text = bottomNavItem.title)
                }
            )
        }
    }
}