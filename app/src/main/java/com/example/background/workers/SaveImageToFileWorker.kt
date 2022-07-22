package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "SavedImageToFileWorker"

class SaveImageToFileWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val title = "Blured Image"
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z")

    override fun doWork(): Result {
        // Makes a notification when the work starts and slows down the work so that
        // it's easier to see each WorkRequest start, even on emulated devices
        makeStatusNotification("Saving image", applicationContext)
        sleep()

        val resolver = applicationContext.contentResolver
        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            val imageUrl =MediaStore.Images.Media.insertImage(resolver, bitmap, title, dateFormatter.format(
                Date()
            ))
            if (!imageUrl.isNullOrEmpty()){
                val output = workDataOf(KEY_IMAGE_URI to imageUrl)
                Result.success(output)
            }else{
                Log.e(TAG, "Writing to MediaStore failed")
                Result.success()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }

}