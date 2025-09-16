package com.example.co_opapp.Service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


//send images as multipart/form data (compress images to jpeg)
fun Uri.toMultipartBody(context: Context, name: String): MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()

        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(name, "avatar.jpg", requestBody)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
