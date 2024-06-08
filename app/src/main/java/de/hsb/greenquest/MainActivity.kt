package de.hsb.greenquest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.hsb.greenquest.ui.Camera.CameraPreviewScreen
import de.hsb.greenquest.ui.navigation.BottomNavigationBar
import de.hsb.greenquest.ui.navigation.GreenQuestTooAppBar
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.screen.ChallengeScreen
import de.hsb.greenquest.ui.screen.PlantDetailScreen
import de.hsb.greenquest.ui.screen.PortfolioScreen
import de.hsb.greenquest.ui.theme.GreenQuestTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermissionsGranted = permissions.all { it.value }
        if (allPermissionsGranted) {
            setCameraPreview()
        } else {
            // Handle permission denial
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request camera, storage, and location permissions
        val cameraPermission = Manifest.permission.CAMERA
        val storagePermission = Manifest.permission.READ_MEDIA_IMAGES
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        val hasCameraPermission = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        val hasStoragePermission = ContextCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_GRANTED
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission && hasStoragePermission && (hasFineLocationPermission || hasCoarseLocationPermission)) {
            // All required permissions are granted
            setCameraPreview()
        } else {
            // Request the missing permissions
            val permissionsToRequest = mutableListOf<String>()
            if (!hasCameraPermission) {
                permissionsToRequest.add(cameraPermission)
            }
            if (!hasStoragePermission) {
                permissionsToRequest.add(storagePermission)
            }
            if (!hasFineLocationPermission) {
                permissionsToRequest.add(fineLocationPermission)
            }
            if (!hasCoarseLocationPermission) {
                permissionsToRequest.add(coarseLocationPermission)
            }

            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }





    @OptIn(ExperimentalMaterial3Api::class)
    private fun setCameraPreview() {
        setContent {
            GreenQuestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFFB69DF8)), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var title by remember { mutableStateOf(Screen.CameraScreen.title) }
                    Scaffold(
                        topBar = {
                            GreenQuestTooAppBar(title = title, canNavigateBack = navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            }
                        },
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        },
                        floatingActionButton = {

                        },
                    ) {
                        innerPadding ->
                        NavHost(navController = navController, startDestination = Screen.CameraScreen.route, Modifier.padding(innerPadding)) {
                            composable(route = Screen.PortfolioScreen.route) {
                                title = Screen.PortfolioScreen.title
                                PortfolioScreen(navController = navController)
                            }
                            composable(
                                route = Screen.PlantDetailScreen.route + "/{plantName}",
                                arguments = listOf(navArgument("plantName") { type = NavType.StringType })
                            ) {
                                title = Screen.PlantDetailScreen.title
                                PlantDetailScreen(navController = navController, name = it.arguments?.getString("plantName"))
                            }
                            composable(route = Screen.CameraScreen.route) {
                                title = Screen.CameraScreen.title
                                CameraPreviewScreen(navController = navController)
                            }
                            composable(route = Screen.ChallengeScreen.route) {
                                title = Screen.ChallengeScreen.title
                                ChallengeScreen(name = "Android")
                            }
                        }
                    }
                }
            }
        }
    }
}


