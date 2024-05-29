package de.hsb.greenquest.ui.Camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.viewmodel.CameraViewModel
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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
    var capturedImagePath by remember { mutableStateOf<String?>(null) } // Track the captured image path

    var plantFileName = remember { mutableStateOf("") }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        if (isCameraOpen) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            Button(onClick = {
                captureImage(imageCapture, context, plantFileName) { imagePath ->
                    capturedImagePath = imagePath
                    isCameraOpen = false // Close the camera after capturing the image
                }
            }) {
                Text(text = "Capture Image")
            }
        } else {
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
                        deleteImage(imagePath)
                        isCameraOpen = true
                        navController.navigate(Screen.CameraScreen.route)
                    }) {
                        Text(text = "Try Again")
                    }

                    Button(onClick = {
                        //TODO API (imagePath)
                        cameraViewModel.savePicture(plantFileName.value)
                        Log.d("plantFileName4", plantFileName.value)
                        navController.navigate(Screen.PortfolioScreen.route)
                    }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}

// Function to delete the image
fun deleteImage(imagePath: String) {
    val imageFile = File(imagePath)
    if (imageFile.exists()) {
        val deleted = imageFile.delete()
        if (deleted) {
            println("Image deleted successfully.")
        } else {
            println("Failed to delete the image.")
        }
    } else {
        println("Image file not found.")
    }
}


private fun displayImage(imageView: ImageView, filePath: String) {
    // Decode the bitmap from the file
    val bitmap = BitmapFactory.decodeFile(filePath)
    Log.d("FILEPATH", filePath)
    // Check if the image needs to be rotated
    val rotationDegrees = 90

    // Rotate the bitmap if needed
    val rotatedBitmap = if (rotationDegrees != 0) {
        rotateBitmap(bitmap, rotationDegrees.toFloat())
    } else {
        bitmap // No rotation needed
    }

    // Set the rotated bitmap to the ImageView
    imageView.setImageBitmap(rotatedBitmap)
}
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}


private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    plantFileName: MutableState<String>,
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
                // Get the most recent image added to the MediaStore
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
                            Log.d("plantFileName2.5", plantFileName.value)
                        }

                        Log.d("plantFileName2", plantFileName.value)
                        val filePath = cursor.getString(dataColumnIndex)
                        println("Image saved successfully at $filePath")
                        Log.d("FILEPATH2", filePath)
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