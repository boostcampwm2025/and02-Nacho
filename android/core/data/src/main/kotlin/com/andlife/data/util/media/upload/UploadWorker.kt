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
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.model.guestbook.UploadState
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.ThumbnailGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlin.coroutines.cancellation.CancellationException
import com.andlife.domain.util.Result as DomainResult


@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val guestBookRepository: GuestBookRepository,
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
        val uriStrings = inputData.getStringArray(UploadKey.MEDIA_URIS)?.toList() ?: emptyList()

        // 방명록 관련 정보 가져오기
        val invitationIdString = inputData.getString(UploadKey.INVITATION_ID)
            ?: return Result.failure(errorData("초대장 ID가 누락되었습니다"))
        val invitationId = invitationIdString.toLongOrNull()
            ?: return Result.failure(errorData("초대장 ID 형식이 잘못되었습니다"))
        val guestBookText = inputData.getString(UploadKey.GUEST_BOOK_TEXT) ?: ""
        val isEditing = inputData.getBoolean(UploadKey.IS_EDITING, false)
        val editingGuestBookIdString = inputData.getString(UploadKey.EDITING_GUEST_BOOK_ID)
        val editingGuestBookId = editingGuestBookIdString?.toLongOrNull()

        return try {
            // Foreground Service 시작
            setForeground(getForegroundInfo())

            // MediaFile 객체 생성
            val mediaFiles = mediaFileProvider.createFromUris(uriStrings)
            var uploadUrls: List<String> = emptyList()

            if (mediaFiles.isNotEmpty()) {
                // 1단계: 미디어 업로드 (0-60%)
                mediaUploader.uploadMediasWithProgress(mediaFiles)
                    .catch { throwable ->
                        Log.e(TAG, "업로드 Flow 오류", throwable)
                        throw throwable
                    }
                    .collect { uploadState ->
                        when (uploadState) {
                            is UploadState.Progress -> {
                                // 업로드 진행률을 0-60% 구간으로 매핑
                                val mappedProgress = (uploadState.percent * 0.6).toInt()
                                updateProgress(
                                    progress = mappedProgress,
                                    currentIndex = uploadState.currentIndex,
                                    totalFiles = uploadState.totalFiles,
                                    currentFileName = uploadState.currentFileName
                                )
                            }

                            is UploadState.Success -> {
                                uploadUrls = uploadState.urls
                            }

                            is UploadState.Failure -> {
                                Log.e(TAG, "업로드 실패: ${uploadState.message}")
                                throw Exception(uploadState.message)
                            }

                            is UploadState.Cancelled -> {
                                Log.d(TAG, "업로드 취소됨")
                                throw CancellationException(UploadNoti.MSG_CANCELLED)
                            }

                            else -> { /* Enqueued 등 기타 상태 */
                            }
                        }
                    }
            } else {
                // 미디어가 없는 경우 업로드 단계 건너뛰기
                Log.d(TAG, "미디어가 없어 업로드 단계를 건너뜁니다")
                updateProgress(progress = 60, currentIndex = 0, totalFiles = 0, currentFileName = "업로드 단계 완료")
            }

            // 2단계: 썸네일 생성 (60-80%)
            val guestBookMediaList = mutableListOf<GuestBookMedia>()
            
            if (mediaFiles.isNotEmpty()) {
                updateProgress(progress = 60, currentIndex = 0, totalFiles = mediaFiles.size, currentFileName = "썸네일 생성 중...")
                
                mediaFiles.forEachIndexed { index, mediaFile ->
                    val url = uploadUrls.getOrNull(index)
                    if (url != null) {
                        val thumbnailUrl = if (mediaFile.mediaType == MediaType.VIDEO) {
                            val thumbnailFile = thumbnailGenerator.generateVideoThumbnail(mediaFile.uriString)
                            thumbnailFile?.absolutePath
                        } else {
                            null
                        }

                        guestBookMediaList.add(
                            GuestBookMedia(
                                id = 0, // 새로 생성되는 미디어는 ID가 0
                                type = mediaFile.mediaType,
                                url = url,
                                thumbnailUrl = thumbnailUrl,
                                durationSeconds = null, // MediaFile에 duration 정보가 없으므로 null
                                displayOrder = index
                            )
                        )
                    }

                    // 썸네일 진행률 업데이트 (60-80%)
                    val thumbnailProgress = 60 + ((index + 1) * 20 / mediaFiles.size)
                    updateProgress(
                        progress = thumbnailProgress,
                        currentIndex = index + 1,
                        totalFiles = mediaFiles.size,
                        currentFileName = "썸네일 생성: ${mediaFile.fileName}"
                    )
                }
            } else {
                // 미디어가 없는 경우 썸네일 생성 단계 건너뛰기
                updateProgress(progress = 80, currentIndex = 0, totalFiles = 0, currentFileName = "썸네일 생성 단계 완료")
            }

            // 3단계: 방명록 생성/수정 (80-100%)
            updateProgress(progress = 80, currentIndex = 0, totalFiles = 1, currentFileName = "방명록 처리 중...")

            val result = if (isEditing && editingGuestBookId != null) {
                guestBookRepository.updateGuestBook(
                    guestBookId = editingGuestBookId,
                    textContent = guestBookText,
                    existingImageIds = emptyList(),
                    existingVideoIds = emptyList(),
                    existingAudioIds = emptyList(),
                    newMedias = guestBookMediaList
                )
            } else {
                guestBookRepository.createGuestBook(
                    invitationId = invitationId,
                    textContent = guestBookText,
                    medias = guestBookMediaList
                )
            }

            updateProgress(progress = 90, currentIndex = 0, totalFiles = 1, currentFileName = "완료 처리 중...")

            when (result) {
                is DomainResult.Success -> {
                    updateProgress(progress = 100, currentIndex = 1, totalFiles = 1, currentFileName = "완료")
                    notificationManager.notifyComplete(id)
                    Result.success(
                        workDataOf(
                            UploadKey.RESULT_URLS to uploadUrls.toTypedArray(),
                            UploadKey.PROGRESS to 100
                        )
                    )
                }

                is DomainResult.Error -> {
                    Result.failure(errorData("방명록 처리 실패: ${result.message ?: "알 수 없는 오류"}"))
                }
            }

        } catch (e: Exception) {
            if (e is CancellationException || isStopped) {
                Log.d(TAG, UploadNoti.MSG_CANCELLED)
                Result.failure(errorData(UploadNoti.MSG_CANCELLED))
            } else {
                Log.e(TAG, "전체 프로세스 중 오류 발생", e)
                Result.failure(errorData(e.message ?: UploadError.UNKNOWN))
            }
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
