package de.hsb.greenquest.data.local.InternalStorage

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject


/**
 * Saves and that belong to donwloaded, therefore active challenge Cards
 * images are saved in the android internal storage as a bitmap
 */
class ChallengeCardImageInternalStorageLoader @Inject constructor(
    private val applicationContext: Context
) {

    /**
     * saves images
     * @param File Image to be saved
     * @return absolute Path of the saved image
     */
    fun saveToInternalStorage(image: File): String{
        val bmOptions = BitmapFactory.Options()
        var bitmapImage = BitmapFactory.decodeFile(image.absolutePath, bmOptions)

        val cw = ContextWrapper(applicationContext);
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val uuid = UUID.randomUUID().toString()
        val mypath = File(directory, "$uuid.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)

            val matrix = Matrix().apply { postRotate(90F) }
            bitmapImage = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.width, bitmapImage.height, matrix, true)

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val abs = mypath.absolutePath
        println("PATH OF FILE IN INTERNAL STORAGE: $abs")
        return mypath.absolutePath
    }

}