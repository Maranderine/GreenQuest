package de.hsb.greenquest

import NearbyConnectionScreen
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import de.hsb.greenquest.ui.screen.CameraPreviewScreen
import de.hsb.greenquest.ui.navigation.BottomNavigationBar
import de.hsb.greenquest.ui.navigation.GreenQuestTooAppBar
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.screen.ChallengeScreen
import de.hsb.greenquest.ui.screen.PlantDetailScreen
import de.hsb.greenquest.ui.screen.PortfolioScreen
import de.hsb.greenquest.ui.screen.SearchCardsScreen
import de.hsb.greenquest.ui.theme.GreenQuestTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val DENIAL_COUNT = "denial_count"
    }
    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermissionsGranted = permissions.all { it.value }
        if (allPermissionsGranted) {
            setCameraPreview()
            println("fksjfkdjmgd")
        } else {
            val sharedPreferences = getSharedPreferences("de.hsb.greenquest", Context.MODE_PRIVATE)
            val denialCount = sharedPreferences.getInt(DENIAL_COUNT, 0)
            if (denialCount >= 2) {
                openAppSettings()
            } else {
                sharedPreferences.edit().putInt(DENIAL_COUNT, denialCount + 1).apply()
                recreate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraPermission = Manifest.permission.CAMERA
        val storagePermission = Manifest.permission.READ_MEDIA_IMAGES
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val bluetoothPermission = Manifest.permission.BLUETOOTH
        val bluetoothAdminPermission = Manifest.permission.BLUETOOTH_ADMIN
        val bluetoothScanPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else null
        val bluetoothAdvertisePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_ADVERTISE else null
        val bluetoothConnectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else null
        val nearbyWifiDevicesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.NEARBY_WIFI_DEVICES else null
        val internetPermission = Manifest.permission.INTERNET
        val accessWifiStatePermission = Manifest.permission.ACCESS_WIFI_STATE
        val changeWifiStatePermission = Manifest.permission.CHANGE_WIFI_STATE

        val hasCameraPermission = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        val hasStoragePermission = ContextCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_GRANTED
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothPermission = ContextCompat.checkSelfPermission(this, bluetoothPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothAdminPermission = ContextCompat.checkSelfPermission(this, bluetoothAdminPermission) == PackageManager.PERMISSION_GRANTED
        val hasBluetoothScanPermission = bluetoothScanPermission?.let { ContextCompat.checkSelfPermission(this, it) } == PackageManager.PERMISSION_GRANTED
        val hasBluetoothAdvertisePermission = bluetoothAdvertisePermission?.let { ContextCompat.checkSelfPermission(this, it) } == PackageManager.PERMISSION_GRANTED
        val hasBluetoothConnectPermission = bluetoothConnectPermission?.let { ContextCompat.checkSelfPermission(this, it) } == PackageManager.PERMISSION_GRANTED
        val hasNearbyWifiDevicesPermission = nearbyWifiDevicesPermission?.let { ContextCompat.checkSelfPermission(this, it) } == PackageManager.PERMISSION_GRANTED
        val hasInternetPermission = ContextCompat.checkSelfPermission(this, internetPermission) == PackageManager.PERMISSION_GRANTED
        val hasAccessWifiStatePermission = ContextCompat.checkSelfPermission(this, accessWifiStatePermission) == PackageManager.PERMISSION_GRANTED
        val hasChangeWifiStatePermission = ContextCompat.checkSelfPermission(this, changeWifiStatePermission) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission && hasStoragePermission && (hasFineLocationPermission || hasCoarseLocationPermission)
            && hasBluetoothPermission && hasBluetoothAdminPermission && (bluetoothScanPermission == null || hasBluetoothScanPermission!!)
            && (bluetoothAdvertisePermission == null || hasBluetoothAdvertisePermission!!)
            && (bluetoothConnectPermission == null || hasBluetoothConnectPermission!!)
            && (nearbyWifiDevicesPermission == null || hasNearbyWifiDevicesPermission!!)
            && hasInternetPermission && hasAccessWifiStatePermission && hasChangeWifiStatePermission) {
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
            if (bluetoothScanPermission != null && !hasBluetoothScanPermission!!) {
                permissionsToRequest.add(bluetoothScanPermission)
            }
            if (bluetoothAdvertisePermission != null && !hasBluetoothAdvertisePermission!!) {
                permissionsToRequest.add(bluetoothAdvertisePermission)
            }
            if (bluetoothConnectPermission != null && !hasBluetoothConnectPermission!!) {
                permissionsToRequest.add(bluetoothConnectPermission)
            }
            if (nearbyWifiDevicesPermission != null && !hasNearbyWifiDevicesPermission!!) {
                permissionsToRequest.add(nearbyWifiDevicesPermission)
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
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
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
                                ChallengeScreen(navController = navController)
                            }
                            composable(route = Screen.SearchCardsScreen.route) {
                                title = Screen.SearchCardsScreen.title
                                SearchCardsScreen(navController = navController)
                            }
                            composable(route = Screen.NearbyShareScreen.route) {
                                title = Screen.NearbyShareScreen.title
                                NearbyConnectionScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}


