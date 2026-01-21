package com.andlife.data.util.media

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.andlife.domain.util.ThumbnailGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import jakarta.inject.Inject

class ThumbnailGeneratorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThumbnailGenerator {

    override suspend fun generateVideoThumbnail(
        videoUriString: String,
        timeUs: Long,
        quality: Int
    ): File? = withContext(Dispatchers.IO) {
        val videoUri = Uri.parse(videoUriString)

        try {
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(context, videoUri)
            }

            val bitmap =
                retriever.getFrameAtTime(
                    timeUs,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                ) ?: return@withContext null

            val thumbnailFile =
                File(context.cacheDir, "thumbnail_${System.currentTimeMillis()}.jpg")

            FileOutputStream(thumbnailFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }

            retriever.release()

            thumbnailFile
        } catch (e: Exception) {
            Log.e("ThumbnailGenerator", "썸네일 생성 실패", e)
            null
        }
    }
}
