package de.hsb.greenquest

import NearbyConnectionScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.hsb.greenquest.ui.Camera.CameraPreviewScreen
import de.hsb.greenquest.ui.navigation.BottomNavigationBar
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
        val bluetoothPermission = Manifest.permission.BLUETOOTH
        val bluetoothAdminPermission = Manifest.permission.BLUETOOTH_ADMIN
        val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN
        val bluetoothAdvertisePermission = Manifest.permission.BLUETOOTH_ADVERTISE
        val bluetoothConnectPermission = Manifest.permission.BLUETOOTH_CONNECT
        val internetPermission = Manifest.permission.INTERNET
        val accessWifiStatePermission = Manifest.permission.ACCESS_WIFI_STATE
        val changeWifiStatePermission = Manifest.permission.CHANGE_WIFI_STATE

        val hasCameraPermission = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        val hasStoragePermission = ContextCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_GRANTED
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothPermission = ContextCompat.checkSelfPermission(this, bluetoothPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothAdminPermission = ContextCompat.checkSelfPermission(this, bluetoothAdminPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothScanPermission = ContextCompat.checkSelfPermission(this, bluetoothScanPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothAdvertisePermission = ContextCompat.checkSelfPermission(this, bluetoothAdvertisePermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothConnectPermission = ContextCompat.checkSelfPermission(this, bluetoothConnectPermission) == PackageManager.PERMISSION_GRANTED
        val hasInternetPermission = ContextCompat.checkSelfPermission(this, internetPermission) == PackageManager.PERMISSION_GRANTED
        val hasAccessWifiStatePermission = ContextCompat.checkSelfPermission(this, accessWifiStatePermission) == PackageManager.PERMISSION_GRANTED
        val hasChangeWifiStatePermission = ContextCompat.checkSelfPermission(this, changeWifiStatePermission) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission && hasStoragePermission && (hasFineLocationPermission || hasCoarseLocationPermission)
            && hasBluetoothPermission && hasBluetoothAdminPermission && hasBluetoothScanPermission
            && hasBluetoothAdvertisePermission && hasBluetoothConnectPermission && hasInternetPermission) {
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
            if (!hasBluetoothPermission) {
                permissionsToRequest.add(bluetoothPermission)
            }
            if (!hasBluetoothAdminPermission) {
                permissionsToRequest.add(bluetoothAdminPermission)
            }
            if (!hasBluetoothScanPermission) {
                permissionsToRequest.add(bluetoothScanPermission)
            }
            if (!hasBluetoothAdvertisePermission) {
                permissionsToRequest.add(bluetoothAdvertisePermission)
            }
            if (!hasBluetoothConnectPermission) {
                permissionsToRequest.add(bluetoothConnectPermission)
            }
            if (!hasInternetPermission) {
                permissionsToRequest.add(internetPermission)
            }
            if (!hasAccessWifiStatePermission) {
                permissionsToRequest.add(accessWifiStatePermission)
            }
            if (!hasChangeWifiStatePermission) {
                permissionsToRequest.add(changeWifiStatePermission)
            }

            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }





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
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        },
                        floatingActionButton = {

                        },
                    ) {
                        innerPadding ->
                        NavHost(navController = navController, startDestination = Screen.NearbyShareScreen.route, Modifier.padding(innerPadding)) {
                            composable(route = Screen.PortfolioScreen.route) {
                                PortfolioScreen(navController = navController)
                            }
                            composable(
                                route = Screen.PlantDetailScreen.route + "/{plantName}",
                                arguments = listOf(navArgument("plantName") { type = NavType.StringType })
                            ) {
                                PlantDetailScreen(navController = navController, name = it.arguments?.getString("plantName"))
                            }
                            composable(route = Screen.CameraScreen.route) {
                                CameraPreviewScreen(navController = navController)
                            }
                            composable(route = Screen.ChallengeScreen.route) {
                                ChallengeScreen(name = "Android")
                            }
                            composable(route = Screen.NearbyShareScreen.route) {
                                NearbyConnectionScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}


