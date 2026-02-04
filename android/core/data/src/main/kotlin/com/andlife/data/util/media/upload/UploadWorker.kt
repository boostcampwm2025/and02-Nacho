package com.andlife.data.util.media.upload

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result as DomainResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException


@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val notificationManager: UploadNotificationManager,
) : CoroutineWorker(context, params) {

    private val uniqueNotificationId: Int by lazy { id.hashCode() }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = notificationManager.createProgressNotification(id, 0)
        return buildForegroundInfo(notification)
    }

    override suspend fun doWork(): Result {
        notificationManager.prepareChannels()

        // URI 리스트 가져오기
        val uriStrings = inputData.getStringArray(UploadKey.MEDIA_URIS)?.toList()
            ?: return Result.failure(errorData(UploadError.MISSING_MEDIA_FILES))

        if (uriStrings.isEmpty()) {
            return Result.failure(errorData(UploadError.MISSING_MEDIA_FILES))
        }

        return try {
            // Foreground Service 시작
            setForeground(getForegroundInfo())

            // MediaFile 객체 생성
            val mediaFiles = mediaFileProvider.createFromUris(uriStrings)

            if (mediaFiles.isEmpty()) {
                return Result.failure(errorData("유효하지 않은 미디어 파일입니다"))
            }

            // 초기 진행률 설정
            updateProgress(
                progress = 0,
                currentIndex = 0,
                totalFiles = mediaFiles.size,
                currentFileName = mediaFiles.first().fileName
            )

            // 업로드 실행
            when (val uploadResult = mediaUploader.uploadMedias(mediaFiles)) {
                is DomainResult.Success -> {
                    val urls = uploadResult.data

                    notificationManager.notifyComplete(id)
                    Result.success(
                        workDataOf(
                            UploadKey.RESULT_URLS to urls.filterNotNull().toTypedArray(),
                            UploadKey.PROGRESS to 100
                        )
                    )
                }

                is DomainResult.Error -> {
                    Log.e(TAG, "업로드 실패: ${uploadResult.message}")
                    Result.failure(errorData(uploadResult.message ?: UploadError.UNKNOWN))
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException || isStopped) {
                Log.d(TAG, UploadNoti.MSG_CANCELLED)
            } else {
                Log.e(TAG, "업로드 중 오류 발생", e)
            }
            Result.failure(errorData(e.message ?: UploadError.UNKNOWN))
        }
    }

    private suspend fun updateProgress(
        progress: Int,
        currentIndex: Int,
        totalFiles: Int,
        currentFileName: String?
    ) {
        setProgress(
            workDataOf(
                UploadKey.PROGRESS to progress,
                UploadKey.CURRENT_FILE_INDEX to currentIndex,
                UploadKey.TOTAL_FILES to totalFiles,
                UploadKey.CURRENT_FILE_NAME to currentFileName
            )
        )

        notificationManager.update(
            id,
            notificationManager.createProgressNotification(
                workId = id,
                progress = progress,
                currentFileName = currentFileName,
                currentIndex = currentIndex,
                totalFiles = totalFiles
            )
        )
    }


    private fun buildForegroundInfo(notification: Notification): ForegroundInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(uniqueNotificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(uniqueNotificationId, notification)
        }
    }

    private fun errorData(message: String): Data {
        return workDataOf(UploadKey.ERROR_MESSAGE to message)
    }

    companion object {
        private const val TAG = "UploadWorker"
    }
}
