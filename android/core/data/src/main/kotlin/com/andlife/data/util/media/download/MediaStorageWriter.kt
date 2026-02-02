package com.andlife.data.util.media.download

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.andlife.domain.model.guestbook.MediaType
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class MediaStorageWriter @Inject constructor(
    private val contentResolver: ContentResolver,
) {

    suspend fun save(
        inputStream: InputStream,
        fileName: String,
        mediaType: MediaType,
        contentLength: Long,
        onProgress: suspend (Int) -> Unit,
        isStopped: () -> Boolean,
    ): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStore(inputStream, fileName, mediaType, contentLength, onProgress, isStopped)
        } else {
            saveToExternalStorage(inputStream, fileName, mediaType, contentLength, onProgress, isStopped)
        }
    }

    private suspend fun saveToMediaStore(
        inputStream: InputStream,
        fileName: String,
        mediaType: MediaType,
        contentLength: Long,
        onProgress: suspend (Int) -> Unit,
        isStopped: () -> Boolean,
    ): String {
        val collection = when (mediaType) {
            MediaType.IMAGE -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaType.VIDEO -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaType.AUDIO -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, resolveMimeType(fileName, mediaType))
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${mediaType.directory}/${DownloadFile.SAVE_DIRECTORY_NAME}")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(collection, contentValues)
            ?: throw IllegalStateException("Media insert 실패")

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                copyWithProgress(inputStream, outputStream, contentLength, onProgress, isStopped)
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            return uri.toString()
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
        onProgress: suspend (Int) -> Unit,
        isStopped: () -> Boolean,
    ): String {
        val publicDir = Environment.getExternalStoragePublicDirectory(mediaType.directory)
        val directory = File(publicDir, DownloadFile.SAVE_DIRECTORY_NAME)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = generateUniqueFile(directory, fileName)
        FileOutputStream(file).use { outputStream ->
            copyWithProgress(inputStream, outputStream, contentLength, onProgress, isStopped)
        }

        return Uri.fromFile(file).toString()
    }

    private suspend fun copyWithProgress(
        inputStream: InputStream,
        outputStream: OutputStream,
        contentLength: Long,
        onProgress: suspend (Int) -> Unit,
        isStopped: () -> Boolean,
    ) {
        val buffer = ByteArray(DownloadFile.BUFFER_SIZE)
        var downloadedBytes = 0L
        var lastProgress = -1

        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            if (isStopped()) break

            outputStream.write(buffer, 0, bytesRead)
            downloadedBytes += bytesRead

            if (contentLength > 0) {
                val progress = ((downloadedBytes * 100) / contentLength).toInt()
                if (progress > lastProgress) {
                    lastProgress = progress
                    onProgress(progress)
                }
            }
        }
        outputStream.flush()
        onProgress(100)
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

    private val MediaType.directory: String
        get() = when (this) {
            MediaType.IMAGE -> Environment.DIRECTORY_PICTURES
            MediaType.VIDEO -> Environment.DIRECTORY_MOVIES
            MediaType.AUDIO -> Environment.DIRECTORY_MUSIC
        }
}
