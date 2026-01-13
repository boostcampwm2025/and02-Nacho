package com.andlife.invitation_card.editor.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import javax.inject.Inject
import kotlin.math.max
import androidx.core.graphics.createBitmap

interface ImageLoader {
    suspend fun loadBitmap(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Result<Bitmap, DataError.LocalImage>
}

class ImageLoaderImpl @Inject constructor() : ImageLoader {

    companion object {
        const val IMAGE_HORIZONTAL_PADDING = 8
    }

    override suspend fun loadBitmap(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Result<Bitmap, DataError.LocalImage> = withContext(Dispatchers.IO) {
        try {
            val sampled = downSample(context, uri, maxWidth, maxHeight) ?: return@withContext Result.Error(DataError.LocalImage.DecodeFailed)
            val corrected = correctOrientation(context, uri, sampled)
            val cropped = cropCenter(corrected, maxWidth - IMAGE_HORIZONTAL_PADDING, maxHeight)
            Result.Success(cropped)
        } catch (e: OutOfMemoryError) {
            Result.Error(DataError.LocalImage.OutOfMemory, e.message)
        } catch (e: FileNotFoundException) {
            Result.Error(DataError.LocalImage.NotFound, e.message)
        } catch (e: Exception) {
            Result.Error(DataError.LocalImage.DecodeFailed, e.message)
        }
    }

    private fun downSample(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri).use {
            BitmapFactory.decodeStream(it, null, options)
        }

        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false

        return context.contentResolver.openInputStream(uri).use {
            BitmapFactory.decodeStream(it, null ,options)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        val (width, height) = options.outWidth to options.outHeight
        var inSampleSize = 1

        if (height > maxHeight || width > maxWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= maxHeight && (halfWidth / inSampleSize) >= maxWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun correctOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val exif = context.contentResolver.openInputStream(uri)?.use {
            ExifInterface(it)
        } ?: return bitmap

        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        if (orientation == ExifInterface.ORIENTATION_NORMAL) return bitmap

        val matrix = Matrix().apply {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
                else -> return bitmap
            }
        }

        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        ).also {
            if (it != bitmap) bitmap.recycle()
        }
    }

    private fun cropCenter(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        if (source.width == targetWidth && source.height == targetHeight) {
            return source
        }

        val scaleX = targetWidth.toFloat() / source.width
        val scaleY = targetHeight.toFloat() / source.height
        val scale = max(scaleX, scaleY)

        val scaledSrcWidth = targetWidth / scale
        val scaledSrcHeight = targetHeight / scale

        val srcLeft = (source.width - scaledSrcWidth) / 2f
        val srcTop = (source.height - scaledSrcHeight) / 2f

        val srcRect = android.graphics.Rect(
            srcLeft.toInt(),
            srcTop.toInt(),
            (srcLeft + scaledSrcWidth).toInt(),
            (srcTop + scaledSrcHeight).toInt()
        )

        val dstRect = android.graphics.Rect(0, 0, targetWidth, targetHeight)

        val output = createBitmap(targetWidth, targetHeight)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }

        canvas.drawBitmap(source, srcRect, dstRect, paint)

        if (source != output && !source.isRecycled) {
            source.recycle()
        }

        return output
    }
}
