package com.andlife.data.util.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
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
import com.andlife.network.di.NachoMedia
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
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    @param:NachoMedia private val okHttpClient: OkHttpClient,
    private val contentResolver: ContentResolver,
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

        val url = inputData.getString(DownloadKey.URL)
            ?: return@withContext Result.failure(errorData(DownloadError.MISSING_URL))
        val fileName =
            inputData.getString(DownloadKey.FILE_NAME)
                ?: return@withContext Result.failure(errorData(DownloadError.MISSING_FILE_NAME))
        val mediaType = inputData.getInt(DownloadKey.MEDIA_TYPE, -1).let { MediaType.entries.getOrNull(it) }
            ?: return@withContext Result.failure(errorData(DownloadError.MISSING_MEDIA_TYPE))

        return@withContext try {
            runCatching { setForeground(getForegroundInfo()) }
                .onFailure { Log.e("DownloadWorker", "Foreground 승격 실패", it) }

            setProgress(workDataOf(DownloadKey.PROGRESS to 0))

            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("${DownloadError.FAILED}${response.code}")

                val body = checkNotNull(response.body) { DownloadError.MISSING_BODY }

                val uri = saveToStorage(
                    inputStream = body.byteStream(),
                    fileName = fileName,
                    mediaType = mediaType,
                    contentLength = body.contentLength(),
                )

                updateCompleteNotification(fileName, mediaType)
                Result.success(workDataOf(DownloadKey.RESULT_URL to uri))
            }
        } catch (e: Exception) {
            if (e is CancellationException || isStopped) {
                Log.d("DownloadWorker", "작업이 취소되었습니다.")
            } else {
                Log.e("DownloadWorker", "다운로드 중 오류 발생", e)
            }
            Result.failure(errorData(e.message ?: DownloadError.UNKNOWN))
        }
    }

    private fun prepareNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return

            val channels = listOf(
                NotificationChannel(
                    DownloadNoti.CHANNEL_ID_PROGRESS,
                    DownloadNoti.CHANNEL_NAME_PROGRESS,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                },
                NotificationChannel(
                    DownloadNoti.CHANNEL_ID_COMPLETE,
                    DownloadNoti.CHANNEL_NAME_COMPLETE,
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
        val cancelIntent = Intent(context, DownloadCancelReceiver::class.java).apply {
            putExtra(DownloadKey.EXTRA_WORK_ID, id.toString())
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, DownloadNoti.CHANNEL_ID_PROGRESS)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(DownloadNoti.TITLE_DOWNLOADING)
            .setContentText(if (progress > 0) "$progress%" else DownloadNoti.MSG_PREPARING)
            .setProgress(100, progress, progress <= 0)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, DownloadNoti.ACTION_CANCEL, cancelPendingIntent)
            .build()
    }

    private fun updateCompleteNotification(fileName: String, mediaType: MediaType) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val prefs = context.getSharedPreferences(DownloadFile.PREF_NAME, Context.MODE_PRIVATE)

        val isAudio = mediaType == MediaType.AUDIO
        val notificationId = if (isAudio) DownloadNoti.ID_COMPLETE_VISUAL else DownloadNoti.ID_COMPLETE_AUDIO
        val countKey = if (isAudio) DownloadFile.KEY_COUNT_AUDIO else DownloadFile.KEY_COUNT_VISUAL

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

        val notification = NotificationCompat.Builder(context, DownloadNoti.CHANNEL_ID_COMPLETE)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(if (isAudio) DownloadNoti.TITLE_COMPLETE_AUDIO else DownloadNoti.TITLE_COMPLETE_VISUAL)
            .setContentText("$currentCount${DownloadNoti.MSG_COMPLETE_SUFFIX}")
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
    ): String {
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
    ): String {
        val contentResolver = contentResolver

        val collection = when (mediaType) {
            MediaType.IMAGE -> MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaType.VIDEO -> MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaType.AUDIO -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(fileName, mediaType))
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${mediaType.directory}/${DownloadFile.SAVE_DIRECTORY_NAME}")
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
    ): String {
        val publicDir = Environment.getExternalStoragePublicDirectory(mediaType.directory)
        val directory = File(publicDir, DownloadFile.SAVE_DIRECTORY_NAME)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = generateUniqueFile(directory, fileName)
        FileOutputStream(file).use { outputStream ->
            copyWithProgress(inputStream, outputStream, contentLength)
        }

        return Uri.fromFile(file).toString()
    }

    private suspend fun copyWithProgress(
        inputStream: InputStream,
        outputStream: OutputStream,
        contentLength: Long,
    ) {
        val buffer = ByteArray(DownloadFile.BUFFER_SIZE)
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
                        DownloadKey.PROGRESS to progress,
                        DownloadKey.DOWNLOADED_BYTES to downloadedBytes,
                        DownloadKey.TOTAL_BYTES to contentLength
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
        setProgress(workDataOf(DownloadKey.PROGRESS to 100))
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
            MediaType.IMAGE -> DownloadFile.MIME_IMAGE
            MediaType.VIDEO -> DownloadFile.MIME_VIDEO
            MediaType.AUDIO -> DownloadFile.MIME_AUDIO
        }
    }

    fun errorData(message: String): Data {
        return workDataOf(DownloadKey.ERROR_MESSAGE to message)
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

                    val folderMimeType =
                        if (mediaType == MediaType.IMAGE) DownloadFile.MIME_IMAGE else DownloadFile.MIME_VIDEO

                    setDataAndType(uri, folderMimeType)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            MediaType.AUDIO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent(Intent.ACTION_VIEW).apply {
                        val rootUri = Uri.parse(DownloadFile.URI_STORAGE_ROOT)
                        setDataAndType(rootUri, DownloadFile.MIME_FOLDER_Q)
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
}
