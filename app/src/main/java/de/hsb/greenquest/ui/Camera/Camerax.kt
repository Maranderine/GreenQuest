package de.hsb.greenquest.ui.Camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.viewmodel.CameraViewModel
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
Tutorial used for the Implementation of The CameraX
https://medium.com/@deepugeorge2007travel/mastering-camerax-in-jetpack-compose-a-comprehensive-guide-for-android-developers-92ec3591a189
*/
@Composable
fun CameraPreviewScreen(navController: NavController) {
    val cameraViewModel = hiltViewModel<CameraViewModel>()

    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    var isCameraOpen by remember { mutableStateOf(true) } // Track if the camera is open
    var isConfirmImage by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var capturedImagePath by remember { mutableStateOf<String?>(null) } // Track the captured image path

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var plantFileName = remember { mutableStateOf("") }
    val shouldNavigate by cameraViewModel._shouldNavigate.collectAsState() // Observe navigation state from ViewModel
    val error by cameraViewModel.error.collectAsState()


    LaunchedEffect(lensFacing) {
        cameraViewModel.plant = null
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    LaunchedEffect(error) {
        error?.let {
            isCameraOpen = true
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            cameraViewModel.resetError()
        }
    }

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            navController.navigate(Screen.PortfolioScreen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            cameraViewModel._shouldNavigate.value = false // Reset navigation state
        }
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        if (isCameraOpen) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            Button(onClick = {
                getCurrentLocation(context, fusedLocationClient) { location ->
                    captureImage(imageCapture, context, plantFileName, location) { imagePath ->
                        capturedImagePath = imagePath
                        isCameraOpen = false // Close the camera after capturing the image
                        isConfirmImage = true
                    }
                }
            }) {
                Text(text = "Capture Image")
            }
        } else if(isConfirmImage) {
            // Display the captured image
            capturedImagePath?.let { imagePath ->
                val imageView = ImageView(context)
                displayImage(imageView, imagePath)

                AndroidView(
                    { imageView },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                )
                Row(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Button(onClick = {
                        cameraViewModel.deleteImage(imagePath)
                        isCameraOpen = true
                        capturedImagePath = null
                        navController.navigate(Screen.CameraScreen.route)
                    }) {
                        Text(text = "Try Again")
                    }

                    Button(onClick = {
                        isConfirmImage = false
                    }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }else{
            //achievements
            capturedImagePath?.let { imagePath ->
                val plant: Plant? = cameraViewModel.plant
                if(plant == null){
                    cameraViewModel.identify(imagePath)
                }


                val imageView = ImageView(context)
                displayImage(imageView, imagePath)
                AndroidView(
                    { imageView },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                )

                var resString: String = "give us a moment to identify the plant..."
                plant?.name?.let { resString = "congrats, you've found a $it !!" }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(text = resString, textAlign = TextAlign.Center, modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            isCameraOpen = true
                            cameraViewModel.plant = null
                            cameraViewModel.savePicture(plantFileName.value, imagePath)
                            navController.navigate(Screen.PortfolioScreen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Text(text = "add to Portfolio")
                        }
                        Button(onClick = {
                            showDialog = true
                            cameraViewModel.plant = null
                        }) {
                            Text(text = "create Challenge Card")
                        }
                    }

                    var text by remember { mutableStateOf("") }

                    if(showDialog){
                        Dialog(onDismissRequest = {
                            showDialog = false
                            isCameraOpen = true
                        }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(Modifier.size(20.dp))
                                    TextField(
                                        value = text,
                                        onValueChange = { text = it },
                                        label = { Text("Optionally: give a hint") }
                                    )
                                    Spacer(Modifier.size(20.dp))
                                    Button(onClick = {
                                        showDialog = false
                                        isCameraOpen = true
                                        cameraViewModel.createChallengeCard(imagePath, text)
                                    }) {
                                        Text(text = "Confirm")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun navigateTo() {

}

// Function to get the current location
private fun getCurrentLocation(context: Context, fusedLocationClient: FusedLocationProviderClient, callback: (Location?) -> Unit) {
    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener {
                callback(null)
            }
    } catch (e: SecurityException) {
        callback(null)
    }
}

// Function to add GPS metadata
private fun addGPSMetadata(filePath: String, location: Location) {
    try {
        val exif = ExifInterface(filePath)
        exif.setGpsInfo(location)
        exif.saveAttributes()
        println("GPS metadata added successfully.")
    } catch (e: Exception) {
        println("Failed to add GPS metadata: ${e.message}")
    }
}


// Function to delete the image



private fun displayImage(imageView: ImageView, filePath: String) {
    // Decode the bitmap from the file
    val bitmap = BitmapFactory.decodeFile(filePath)

    // Check if the bitmap was successfully decoded
    if (bitmap != null) {
        Log.d("FILEPATH", filePath)

        // Check if the image needs to be rotated
        val rotationDegrees = 90

        // Rotate the bitmap if needed
        val rotatedBitmap = if (rotationDegrees != 0) {
            rotateBitmap(bitmap, rotationDegrees.toFloat())
        } else {
            bitmap
        }

        // Set the rotated bitmap to the ImageView
        imageView.setImageBitmap(rotatedBitmap)
    } else {
        // Handle the case where the bitmap could not be decoded
        Log.e("displayImage", "Failed to decode bitmap from file: $filePath")
        // You might want to display a placeholder image or show an error message here
    }
}
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    plantFileName: MutableState<String>,
    location: Location?,
    onImageCaptured: (String) -> Unit
) {
    val name = "GreenQuest.jpeg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GreenQuest")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri
                val cursor = context.contentResolver.query(
                    savedUri!!,
                    arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME
                    ),
                    null,
                    null,
                    "${MediaStore.Images.Media.DATE_ADDED} DESC",
                    null
                )
                cursor?.use { cursor ->
                    val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val nameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

                    if (dataColumnIndex != -1 && cursor.moveToFirst()) {
                        if (cursor.getString(nameColumnIndex) != "") {
                            plantFileName.value = cursor.getString(nameColumnIndex)
                        }

                        val filePath = cursor.getString(dataColumnIndex)
                        println("Image saved successfully at $filePath")

                        location?.let {
                            addGPSMetadata(filePath, it)
                        }
                        onImageCaptured(filePath) // Call the callback with the correct file path
                    } else {
                        println("Unable to retrieve file path.")
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                println("Failed $exception")
            }
        })

}


private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

fun ExifInterface.setGpsInfo(location: Location) {
    val lat = location.latitude
    val lon = location.longitude

    val latRef = if (lat < 0) "S" else "N"
    val lonRef = if (lon < 0) "W" else "E"

    val absLat = Math.abs(lat)
    val absLon = Math.abs(lon)

    val latDeg = absLat.toInt()
    val latMin = ((absLat - latDeg) * 60).toInt()
    val latSec = (((absLat - latDeg) * 60 - latMin) * 60 * 1000).toInt()

    val lonDeg = absLon.toInt()
    val lonMin = ((absLon - lonDeg) * 60).toInt()
    val lonSec = (((absLon - lonDeg) * 60 - lonMin) * 60 * 1000).toInt()

    setAttribute(ExifInterface.TAG_GPS_LATITUDE, "$latDeg/1,$latMin/1,$latSec/1000")
    setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latRef)
    setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "$lonDeg/1,$lonMin/1,$lonSec/1000")
    setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lonRef)
    setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, location.time.toString())
    setAttribute(ExifInterface.TAG_GPS_DATESTAMP, android.text.format.DateFormat.format("yyyy:MM:dd", location.time).toString())
}