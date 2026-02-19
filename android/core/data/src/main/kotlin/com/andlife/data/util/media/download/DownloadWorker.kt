package com.andlife.data.util.media.download

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.util.Log
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
import kotlinx.coroutines.job
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    @param:NachoMedia private val okHttpClient: OkHttpClient,
    private val mediaStorageWriter: MediaStorageWriter,
    private val notificationManager: DownloadNotificationManager,
) : CoroutineWorker(context, params) {

    private val uniqueNotificationId: Int by lazy { id.hashCode() }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val fileName = inputData.getString(DownloadKey.FILE_NAME) ?: ""
        val notification = notificationManager.createProgressNotification(id, fileName, 0)
        return buildForegroundInfo(notification)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        notificationManager.prepareChannels()

        val url = inputData.getString(DownloadKey.URL)
            ?: return@withContext Result.failure(errorData(DownloadError.MISSING_URL))
        val fileName = inputData.getString(DownloadKey.FILE_NAME)
            ?: return@withContext Result.failure(errorData(DownloadError.MISSING_FILE_NAME))
        val mediaType = inputData.getInt(DownloadKey.MEDIA_TYPE, -1)
            .let { MediaType.entries.getOrNull(it) }
            ?: return@withContext Result.failure(errorData(DownloadError.MISSING_MEDIA_TYPE))

        return@withContext try {
            runCatching { setForeground(getForegroundInfo()) }
                .onFailure { Log.e(TAG, "Foreground 승격 실패", it) }

            setProgress(workDataOf(DownloadKey.PROGRESS to 0))

            val request = Request.Builder().url(url).build()
            val call = okHttpClient.newCall(request)
            coroutineContext.job.invokeOnCompletion { call.cancel() }
            call.execute().use { response ->

                if (!response.isSuccessful) throw Exception("${DownloadError.FAILED}${response.code}")

                val body = checkNotNull(response.body) { DownloadError.MISSING_BODY }

                val uri = mediaStorageWriter.save(
                    inputStream = body.byteStream(),
                    fileName = fileName,
                    mediaType = mediaType,
                    contentLength = body.contentLength(),
                    onProgress = { progress -> updateProgress(fileName, progress) },
                    isStopped = { isStopped },
                )

                val contentUri = if (uri.startsWith(FileConstants.FILE_SCHEMA)) {
                    scanToContentUri(Uri.parse(uri).path ?: "") ?: uri
                } else {
                    uri
                }
                notificationManager.notifyComplete(fileName, mediaType, contentUri)

                Result.success(workDataOf(DownloadKey.RESULT_URL to uri))
            }
        } catch (e: Exception) {
            if (e is CancellationException || isStopped) {
                Log.d(TAG, DownloadNoti.MSG_CANCELLED)
            } else {
                Log.e(TAG, "다운로드 중 오류 발생", e)
            }
            Result.failure(errorData(e.message ?: DownloadError.UNKNOWN))
        }
    }

    private suspend fun updateProgress(fileName: String, progress: Int) {
        val progressData = workDataOf(
            DownloadKey.PROGRESS to progress,
        )
        setProgress(progressData)

        val notification = notificationManager.createProgressNotification(id, fileName, progress)
        try {
            setForeground(buildForegroundInfo(notification))
        } catch (e: Exception) {
            Log.e(TAG, "알림 갱신 실패", e)
        }
    }

    private fun buildForegroundInfo(notification: Notification): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(uniqueNotificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(uniqueNotificationId, notification)
        }
    }

    fun errorData(message: String): Data {
        return workDataOf(DownloadKey.ERROR_MESSAGE to message)
    }

    private suspend fun scanToContentUri(filePath: String): String? =
        suspendCancellableCoroutine { cont ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(filePath),
                null
            ) { _, contentUri ->
                cont.resume(contentUri?.toString())
            }
        }

    companion object {
        private const val TAG = "DownloadWorker"
    }
}
