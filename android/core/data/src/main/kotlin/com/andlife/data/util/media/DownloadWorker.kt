package com.andlife.data.util.media

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.network.di.InvitationMedia
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    @param:InvitationMedia private val okHttpClient: OkHttpClient,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString(KEY_URL) ?: return@withContext Result.failure(errorData("URL이 없습니다"))
        val fileName = inputData.getString(KEY_FILE_NAME) ?: return@withContext Result.failure(errorData("파일명이 없습니다"))
        val mediaTypeOrdinal = inputData.getInt(KEY_MEDIA_TYPE, -1)

        if (mediaTypeOrdinal == -1) {
            return@withContext Result.failure(errorData("미디어 타입이 없습니다"))
        }

        val mediaType = MediaType.entries[mediaTypeOrdinal]

        try {
            setProgress(workDataOf(KEY_PROGRESS to 0))

            val request = Request.Builder().url(url).build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(errorData("다운로드 실패: ${response.code}"))
                }

                val body = response.body ?: return@withContext Result.failure(errorData("응답 본문이 없습니다."))

                val contentLength = body.contentLength()
                val inputStream = body.byteStream()

                val (uri, path) = saveToStorage(
                    inputStream = inputStream,
                    fileName = fileName,
                    mediaType = mediaType,
                    contentLength = contentLength,
                )

                Result.success(
                    workDataOf(
                        KEY_RESULT_URL to uri,
                        KEY_RESULT_PATH to path,
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(errorData(e.message ?: "알 수 없는 오류"))
        }
    }

    private suspend fun saveToStorage(
        inputStream: InputStream,
        fileName: String,
        mediaType: MediaType,
        contentLength: Long,
    ): Pair<String, String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStore(inputStream, fileName, mediaType, contentLength)
        } else {
            saveToExternalStorage(inputStream, fileName, mediaType, contentLength)
        }
    }

    private suspend fun saveToMediaStore(
        inputStream: InputStream,
        fileName: String,
        mediaType: MediaType,
        contentLength: Long,
    ): Pair<String, String> {
        val contentResolver = context.contentResolver

        val collection = when (mediaType) {
            MediaType.IMAGE -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaType.VIDEO -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaType.AUDIO -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(fileName, mediaType))
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${mediaType.directory}/Nacho")
            put(MediaStore.MediaColumns.IS_PENDING, 1)

        }

        val uri = contentResolver.insert(collection, contentValues) ?: throw IllegalStateException("Media insert 실패")

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                copyWithProgress(inputStream, outputStream, contentLength)
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            return Pair(uri.toString(), getPathFromUri(uri))

        } catch (e: Exception) {
            contentResolver.delete(uri, null, null)
            throw e
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun saveToExternalStorage(
        inputStream: InputStream,
        fileName: String,
        mediaType: MediaType,
        contentLength: Long,
    ): Pair<String, String> {
        val publicDir = Environment.getExternalStoragePublicDirectory(mediaType.directory)
        val directory = File(publicDir, "Nacho")

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = generateUniqueFile(directory, fileName)
        FileOutputStream(file).use { outputStream ->
            copyWithProgress(inputStream, outputStream, contentLength)
        }

        return Pair(Uri.fromFile(file).toString(), file.absolutePath)
    }

    private suspend fun copyWithProgress(
        inputStream: InputStream,
        outputStream: OutputStream,
        contentLength: Long,
    ) {
        val buffer = ByteArray(BUFFER_SIZE)
        var downloadedBytes = 0L
        var lastProgress = 0

        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            if (isStopped) break
            outputStream.write(buffer, 0, bytesRead)
            downloadedBytes += bytesRead

            val builder = Data.Builder()
                .putLong(KEY_DOWNLOADED_BYTES, downloadedBytes)
                .putLong(KEY_TOTAL_BYTES, contentLength)

            if (contentLength > 0) {
                val progress = ((downloadedBytes * 100) / contentLength).toInt()

                if (progress > lastProgress) {
                    lastProgress = progress
                    builder.putInt(KEY_PROGRESS, progress)
                }
            }
            setProgress(builder.build())
        }
        outputStream.flush()
        setProgress(Data.Builder().putInt(KEY_PROGRESS, 100).build())
    }

    private fun generateUniqueFile(directory: File, fileName: String): File {
        var file = File(directory, fileName)
        if (!file.exists()) return file

        val nameWithoutExtension = fileName.substringBeforeLast(".")
        val extension = fileName.substringAfterLast(".", "")

        var counter = 1
        while (file.exists()) {
            val newName = if (extension.isNotEmpty()) {
                "$nameWithoutExtension($counter).$extension"
            } else {
                "$nameWithoutExtension($counter)"
            }
            file = File(directory, newName)
            counter++
        }
        return file
    }

    private fun getMimeType(fileName: String, mediaType: MediaType): String {
        val extension = fileName.substringAfterLast(".", "")
        val mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(extension.lowercase())

        return mimeType ?: when (mediaType) {
            MediaType.IMAGE -> "image/*"
            MediaType.VIDEO -> "video/*"
            MediaType.AUDIO -> "audio/*"
        }
    }

    private fun getPathFromUri(uri: Uri): String {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.DATA),
                null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                } else uri.toString()
            } ?: uri.toString()
        } catch (e: Exception) {
            uri.toString()
        }
    }

    fun errorData(message: String): Data {
        return workDataOf(KEY_ERROR_MESSAGE to message)
    }

    private val MediaType.directory: String
        get() = when (this) {
            MediaType.IMAGE -> Environment.DIRECTORY_PICTURES
            MediaType.VIDEO -> Environment.DIRECTORY_MOVIES
            MediaType.AUDIO -> Environment.DIRECTORY_MUSIC
        }

    companion object {
        const val KEY_URL = "url"
        const val KEY_FILE_NAME = "file_name"
        const val KEY_MEDIA_TYPE = "media_type"

        const val KEY_RESULT_URL = "result_url"
        const val KEY_RESULT_PATH = "result_path"
        const val KEY_ERROR_MESSAGE = "error_message"

        const val KEY_PROGRESS = "progress"
        const val KEY_DOWNLOADED_BYTES = "downloaded_bytes"
        const val KEY_TOTAL_BYTES = "total_bytes"

        private const val BUFFER_SIZE = 8 * 1024
    }
}
