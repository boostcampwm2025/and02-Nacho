package com.andlife.data.util.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
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

    private val uniqueNotificationId: Int by lazy { id.hashCode() }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = createDownloadNotification(0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(uniqueNotificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(uniqueNotificationId, notification)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        prepareNotificationChannels()

        val url = inputData.getString(KEY_URL) ?: return@withContext Result.failure(errorData(ERROR_MISSING_URL))
        val fileName =
            inputData.getString(KEY_FILE_NAME) ?: return@withContext Result.failure(errorData(ERROR_MISSING_FILE_NAME))
        val mediaType = inputData.getInt(KEY_MEDIA_TYPE, -1).let { MediaType.entries.getOrNull(it) }
            ?: return@withContext Result.failure(errorData(ERROR_MISSING_MEDIA_TYPE))

        return@withContext try {
            runCatching { setForeground(getForegroundInfo()) }
                .onFailure { Log.e("DownloadWorker", "Foreground 승격 실패", it) }

            setProgress(workDataOf(KEY_PROGRESS to 0))

            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("$ERROR_DOWNLOAD_FAILED${response.code}")

                val body = checkNotNull(response.body) { ERROR_MISSING_BODY }

                val (uri, path) = saveToStorage(
                    inputStream = body.byteStream(),
                    fileName = fileName,
                    mediaType = mediaType,
                    contentLength = body.contentLength(),
                )

                updateCompleteNotification(fileName, mediaType)
                Result.success(workDataOf(KEY_RESULT_URL to uri, KEY_RESULT_PATH to path))
            }
        } catch (e: Exception) {
            Log.e("DownloadWorker", "다운로드 중 오류 발생", e)
            Result.failure(errorData(e.message ?: ERROR_UNKNOWN))
        }
    }

    private fun prepareNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_PROGRESS,
                    CHANNEL_NAME_PROGRESS,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                },
                NotificationChannel(
                    CHANNEL_ID_COMPLETE,
                    CHANNEL_NAME_COMPLETE,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    setSound(null, null)
                    setShowBadge(true)
                }
            )
            manager.createNotificationChannels(channels)
        }
    }

    private fun createDownloadNotification(progress: Int): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID_PROGRESS)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(TITLE_DOWNLOADING)
            .setContentText(if (progress > 0) "$progress%" else MSG_PREPARING)
            .setProgress(100, progress, progress <= 0)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun updateCompleteNotification(fileName: String, mediaType: MediaType) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val isAudio = mediaType == MediaType.AUDIO
        val notificationId = if (isAudio) COMPLETE_AUDIO_NOTIFICATION_ID else COMPLETE_VISUAL_NOTIFICATION_ID
        val countKey = if (isAudio) KEY_COUNT_AUDIO else KEY_COUNT_VISUAL

        val currentCount = synchronized(DownloadWorker::class.java) {
            val activeNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.activeNotifications.any { it.id == notificationId }
            } else {
                true
            }

            val newCount = if (!activeNotification) 1 else prefs.getInt(countKey, 0) + 1
            prefs.edit().putInt(countKey, newCount).apply()
            newCount
        }

        val viewIntent = context.createFolderViewIntent(mediaType, fileName)
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_COMPLETE)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(if (isAudio) TITLE_COMPLETE_AUDIO else TITLE_COMPLETE_VISUAL)
            .setContentText("$currentCount$MSG_COMPLETE_SUFFIX")
            .setSubText(fileName)
            .setNumber(currentCount)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d("DownloadWorker", "알림 전송 완료: ID=$notificationId, 현재 카운트=$currentCount")
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
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${mediaType.directory}/${SAVE_DIRECTORY_NAME}")
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
        val directory = File(publicDir, SAVE_DIRECTORY_NAME)

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
        var lastProgress = -1

        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            if (isStopped) break

            outputStream.write(buffer, 0, bytesRead)
            downloadedBytes += bytesRead

            if (contentLength > 0) {
                val progress = ((downloadedBytes * 100) / contentLength).toInt()

                if (progress > lastProgress) {
                    lastProgress = progress

                    val progressData = workDataOf(
                        KEY_PROGRESS to progress,
                        KEY_DOWNLOADED_BYTES to downloadedBytes,
                        KEY_TOTAL_BYTES to contentLength
                    )

                    setProgress(progressData)

                    val notification = createDownloadNotification(progress)
                    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ForegroundInfo(
                            uniqueNotificationId,
                            notification,
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                        )
                    } else {
                        ForegroundInfo(uniqueNotificationId, notification)
                    }

                    try {
                        setForeground(info)
                    } catch (e: Exception) {
                        Log.e("DownloadWorker", "알림 갱신 실패", e)
                    }
                }
            }
        }
        outputStream.flush()
        setProgress(workDataOf(KEY_PROGRESS to 100))
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
            MediaType.IMAGE -> MIME_TYPE_IMAGE_ALL
            MediaType.VIDEO -> MIME_TYPE_VIDEO_ALL
            MediaType.AUDIO -> MIME_TYPE_AUDIO_ALL
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

    fun Context.createFolderViewIntent(mediaType: MediaType, fileName: String): Intent {
        val specificMimeType = getMimeType(fileName, mediaType)

        return when (mediaType) {
            MediaType.IMAGE, MediaType.VIDEO -> {
                Intent(Intent.ACTION_VIEW).apply {
                    val uri = if (mediaType == MediaType.IMAGE)
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    else MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                    val folderMimeType = if (mediaType == MediaType.IMAGE) MIME_TYPE_IMAGE_ALL else MIME_TYPE_VIDEO_ALL

                    setDataAndType(uri, folderMimeType)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            MediaType.AUDIO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent(Intent.ACTION_VIEW).apply {
                        val rootUri = Uri.parse(URI_EXTERNAL_STORAGE_ROOT)
                        setDataAndType(rootUri, MIME_TYPE_FOLDER_ANDROID_Q)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                } else {
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = specificMimeType
                        addCategory(Intent.CATEGORY_OPENABLE)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            }
        }
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

        const val CHANNEL_ID_PROGRESS = "download_progress_channel"
        const val CHANNEL_ID_COMPLETE = "download_complete_channel"

        const val COMPLETE_VISUAL_NOTIFICATION_ID = 1001
        const val COMPLETE_AUDIO_NOTIFICATION_ID = 1002

        const val CHANNEL_NAME_PROGRESS = "다운로드 진행 상태"
        const val CHANNEL_NAME_COMPLETE = "다운로드 완료 안내"

        const val TITLE_DOWNLOADING = "다운로드 중"
        const val MSG_PREPARING = "파일을 저장하고 있습니다."
        const val TITLE_COMPLETE_AUDIO = "음성 메시지 저장 완료"
        const val TITLE_COMPLETE_VISUAL = "미디어 저장 완료"
        const val MSG_COMPLETE_SUFFIX = "개의 파일이 저장되었습니다."

        const val SAVE_DIRECTORY_NAME = "나에게로의 초대"

        private const val PREF_NAME = "download_prefs"
        private const val KEY_COUNT_AUDIO = "audio_count"
        private const val KEY_COUNT_VISUAL = "visual_count"

        private const val BUFFER_SIZE = 8 * 1024

        private const val ERROR_MISSING_URL = "URL이 없습니다"
        private const val ERROR_MISSING_FILE_NAME = "파일명이 없습니다"
        private const val ERROR_MISSING_MEDIA_TYPE = "미디어 타입이 없습니다"
        private const val ERROR_MISSING_BODY = "응답 본문이 없습니다."
        private const val ERROR_DOWNLOAD_FAILED = "다운로드 실패: "
        private const val ERROR_UNKNOWN = "알 수 없는 오류"

        private const val MIME_TYPE_IMAGE_ALL = "image/*"
        private const val MIME_TYPE_VIDEO_ALL = "video/*"
        private const val MIME_TYPE_AUDIO_ALL = "audio/*"
        private const val MIME_TYPE_FOLDER_ANDROID_Q = "vnd.android.document/root"
        private const val URI_EXTERNAL_STORAGE_ROOT = "content://com.android.externalstorage.documents/root/primary"
    }
}
