package com.andlife.data.util.media

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

interface ImageCompressor {
    suspend fun compressImage(
        uri: Uri,
        quality: Int = 80,
        maxWidth: Int = 2560,
    ): ByteArray?
}

class ImageCompressorImpl @Inject constructor(
    private val contentResolver: ContentResolver,
) : ImageCompressor {
    override suspend fun compressImage(
        uri: Uri,
        quality: Int,
        maxWidth: Int,
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val inputBytes = contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: return@withContext null

            ensureActive()

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
                    ?: return@withContext null

                ensureActive()

                val compressFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                }

                ByteArrayOutputStream().use { outputStream ->
                    bitmap.compress(compressFormat, quality, outputStream)
                    bitmap.recycle()
                    outputStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
