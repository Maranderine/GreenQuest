package de.hsb.greenquest


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.hsb.greenquest.ui.navigation.BottomNavigationBar
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.screen.CategoryScreen
import de.hsb.greenquest.ui.screen.PortfolioScreen
import androidx.core.content.ContextCompat
import de.hsb.greenquest.ui.Camera.CameraPreviewScreen
import de.hsb.greenquest.ui.theme.GreenQuestTheme

class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setCameraPreview()
            } else {
                //this.finish() //TODO
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                setCameraPreview()
            }
            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun setCameraPreview() {
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
                                CategoryScreen(navController = navController)
                            }
                            composable(route = Screen.CameraScreen.route) {
                                CameraPreviewScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }

}