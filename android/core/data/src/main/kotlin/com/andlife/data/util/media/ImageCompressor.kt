package com.andlife.data.util.media

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import androidx.core.graphics.scale

private object Constants {
    const val COMPRESS_QUALITY = 80
    const val COMPRESS_MAX_WIDTH = 2560
}

interface ImageCompressor {
    suspend fun compressImage(
        uri: Uri,
        quality: Int = Constants.COMPRESS_QUALITY,
        maxWidth: Int = Constants.COMPRESS_MAX_WIDTH,
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

            val exifOrientation = contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)

                ensureActive()

                val rotatedBitmap = when (exifOrientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        val matrix = Matrix().apply { postRotate(90f) }
                        val rotated = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                        bitmap.recycle()
                        rotated
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        val matrix = Matrix().apply { postRotate(180f) }
                        val rotated = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                        bitmap.recycle()
                        rotated
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        val matrix = Matrix().apply { postRotate(270f) }
                        val rotated = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                        bitmap.recycle()
                        rotated
                    }
                    else -> bitmap
                }

                ensureActive()

                val resizedBitmap = if (rotatedBitmap.width > maxWidth) {
                    val scaleFactor = maxWidth.toFloat() / rotatedBitmap.width
                    val newHeight = (rotatedBitmap.height * scaleFactor).toInt()
                    val scaled = rotatedBitmap.scale(maxWidth, newHeight)
                    rotatedBitmap.recycle()
                    scaled
                } else {
                    rotatedBitmap
                }

                ensureActive()

                val compressFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                }

                ByteArrayOutputStream().use { outputStream ->
                    resizedBitmap.compress(compressFormat, quality, outputStream)
                    resizedBitmap.recycle()
                    outputStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
