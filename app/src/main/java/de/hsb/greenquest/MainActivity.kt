package de.hsb.greenquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.hsb.greenquest.ui.navigation.BottomNavigationBar
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.screen.CategorieScreen
import de.hsb.greenquest.ui.screen.PortfolioScreen
import de.hsb.greenquest.ui.theme.GreenQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenQuestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        },
                        floatingActionButton = {

                        },
                    ) {
                        innerPadding ->
                        NavHost(navController = navController, startDestination = Screen.PortfolioScreen.route, Modifier.padding(innerPadding)) {
                            composable(route = Screen.PortfolioScreen.route) {
                                PortfolioScreen(navController = navController)
                            }
                            composable(route = Screen.PortfolioCategoryScreen.route) {
                                CategorieScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GreenQuestTheme {
        Greeting("Android")
    }
}