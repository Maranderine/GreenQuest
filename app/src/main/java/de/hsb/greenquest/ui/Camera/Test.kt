package de.hsb.greenquest.ui.Camera

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

@Composable
fun ImageGalleryApp() {
    var hasPermission by remember { mutableStateOf(false) }
    var imageList by remember { mutableStateOf(listOf<Uri>()) }
    val context = LocalContext.current

    val filepath = "/storage/emulated/0/Pictures/CameraX-Image/CameraxImage (1).jpeg"
    //put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")

    val bitmap = BitmapFactory.decodeFile(filepath)
    
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "walla",
        Modifier.rotate(90F)
    )


//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            hasPermission = isGranted
//            if (isGranted) {
//                imageList = loadImagesFromExternalStorage(context = context)
//            } else {
//                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    )
//
//    LaunchedEffect(Unit) {
//        when (PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) -> {
//                hasPermission = true
//                imageList = loadImagesFromExternalStorage(context = context)
//            }
//            else -> {
//                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
//            }
//        }
//    }
//
//    if (hasPermission) {
//        ImageGrid(imageList)
//    } else {
//        Text("Please grant storage permission to view images.")
//    }
}

@Composable
fun ImageGrid(imageList: List<Uri>) {
    Log.d("IMAGES", imageList.toString())
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(imageList.size) { index ->
            val imageUri = imageList[index]
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

fun loadImagesFromExternalStorage(context: Context): List<Uri> {
    val imageList = mutableListOf<Uri>()

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.RELATIVE_PATH
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val query = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            imageList.add(contentUri)
        }
    }

    return imageList
}