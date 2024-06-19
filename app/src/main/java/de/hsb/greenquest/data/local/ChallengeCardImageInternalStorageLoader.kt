package de.hsb.greenquest.data.local

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject


class ChallengeCardImageInternalStorageLoader @Inject constructor(
    private val applicationContext: Context
) {

    fun saveToInternalStorage(image: File): String{
        //create bitmap from temp file
        val bmOptions = BitmapFactory.Options()
        var bitmapImage = BitmapFactory.decodeFile(image.absolutePath, bmOptions)

        val cw = ContextWrapper(applicationContext);
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val uuid = UUID.randomUUID().toString()
        val mypath = File(directory, "$uuid.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
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