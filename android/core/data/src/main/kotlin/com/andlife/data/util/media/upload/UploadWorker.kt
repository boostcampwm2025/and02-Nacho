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
import com.andlife.domain.model.guestbook.MediaFile
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.model.guestbook.UploadState
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result as DomainResult
import com.andlife.domain.util.ThumbnailGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.json.Json

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

        // 방명록 관련 정보 가져오기
        val invitationId = inputData.getLong(UploadKey.INVITATION_ID, -1L)
        val guestBookText = inputData.getString(UploadKey.GUEST_BOOK_TEXT) ?: ""
        val editingGuestBookId = inputData.getLong(UploadKey.EDITING_GUEST_BOOK_ID, -1L)

        // 미디어 관련 정보 가져오기
        val selectedMediasId: List<Long?> =
            Json.decodeFromString(inputData.getString(UploadKey.MEDIA_IDS) ?: "")
        val selectedMediasUri: List<String> = Json.decodeFromString(inputData.getString(UploadKey.MEDIA_URIS) ?: "")
        val selectedMediasType: List<String> = Json.decodeFromString(inputData.getString(UploadKey.MEDIA_TYPES) ?: "")
        val selectedMediasDuration: List<Int?> =
            Json.decodeFromString(inputData.getString(UploadKey.MEDIA_DURATIONS) ?: "")
        val thumbnailUrls: List<String?> =
            Json.decodeFromString(inputData.getString(UploadKey.MEDIA_THUMBNAIL_URLS) ?: "")

        // 미디어 관련 정보를 GuestBookMedia 객체 리스트로 변환
        val guestBookMedias: MutableList<GuestBookMedia> = selectedMediasType.mapIndexed { index, mediaTypeString ->
            val mediaType = when (mediaTypeString) {
                MediaType.IMAGE.name -> MediaType.IMAGE
                MediaType.VIDEO.name -> MediaType.VIDEO
                MediaType.AUDIO.name -> MediaType.AUDIO
                else -> throw IllegalArgumentException("알 수 없는 미디어 타입: ${selectedMediasType.getOrNull(index)}")
            }

            GuestBookMedia(
                id = selectedMediasId[index],
                type = mediaType,
                url = selectedMediasUri[index],
                thumbnailUrl = thumbnailUrls[index],
                durationSeconds = selectedMediasDuration[index],
                displayOrder = index
            )
        }.toMutableList()

        return try {
            // Foreground Service 시작
            setForeground(getForegroundInfo())

            // 1단계: 미디어 업로드 (0-60%)
            val indexAndFiles = mutableListOf<Pair<Int, MediaFile>>()
            guestBookMedias.forEachIndexed { index, media ->
                // 새로 추가된 미디어인 경우 MediaFile 객체 생성하여 업로드 대상에 추가
                if (media.id == null) {
                    mediaFileProvider.createFromUri(media.url)?.let { file ->
                        indexAndFiles.add(index to file)
                    }
                }
            }

            // 새 미디어가 있는 경우만 업로드 수행
            if (indexAndFiles.isNotEmpty()) {
                mediaUploader.uploadMediasWithProgress(indexAndFiles.map { it.second })
                    .collect { state ->
                        when (state) {
                            is UploadState.Progress -> {
                                updateProgress(
                                    progress = (state.percent * 0.6).toInt(),
                                    currentFileName = state.currentFileName,
                                    currentOrder = state.currentOrder,
                                    totalCount = state.totalCount,
                                )
                            }

                            is UploadState.Success -> {
                                // state.urls에는 업로드된 미디어들의 URL이 순서대로 담겨있음(null 허용)
                                // 업로드된 URL을 guestBookMedias에 반영
                                indexAndFiles.forEachIndexed { uploadedIndex, pair ->
                                    val originalIndex = pair.first
                                    guestBookMedias[originalIndex] = guestBookMedias[originalIndex].copy(
                                        url = state.urls.getOrNull(uploadedIndex)
                                            ?: "" // TODO: 업로드된 URL이 null인 경우 빈 문자열이 저장되고 있음
                                    )
                                }
                            }

                            is UploadState.Failure -> throw Exception(state.message)
                            is UploadState.Cancelled -> throw CancellationException(UploadNoti.MSG_CANCELLED)
                            else -> {}
                        }
                    }
            } else {
                // 새 미디어가 없는 경우 업로드 단계 건너뛰기
            }

            // 2단계: 썸네일 생성 (60-70%)
            val indexAndThumbnailFiles = mutableListOf<Pair<Int, MediaFile>>()
            guestBookMedias.forEachIndexed { index, media ->
                    val thumbnailFile = thumbnailGenerator.generateVideoThumbnail(media.url)
                // 새로 추가된 영상인 경우만 썸네일 생성
                if (media.type == MediaType.VIDEO && media.id == null) {
                    if (thumbnailFile != null) {
                        indexAndThumbnailFiles.add(index to mediaFileProvider.createFromFile(thumbnailFile))
                    }
                }
                updateProgress(
                    progress = 60 + ((index + 1) / guestBookMedias.size * 10),
                    currentFileName = "썸네일 생성 중",
                    currentOrder = index + 1,
                    totalCount = guestBookMedias.size,
                )
            }

            // 3단계: 썸네일 업로드 (70-80%)
            if (indexAndThumbnailFiles.isNotEmpty()) {
                mediaUploader.uploadMediasWithProgress(indexAndThumbnailFiles.map { it.second })
                    .collect { state ->
                        when (state) {
                            is UploadState.Progress -> {
                                updateProgress(
                                    progress = 70 + (state.percent * 0.1).toInt(),
                                    currentFileName = state.currentFileName,
                                    currentOrder = state.currentOrder,
                                    totalCount = state.totalCount,
                                )
                            }

                            is UploadState.Success -> {
                                // 업로드된 썸네일 URL을 guestBookMedias에 반영
                                indexAndThumbnailFiles.forEachIndexed { uploadedIndex, pair ->
                                    val originalIndex = pair.first
                                    guestBookMedias[originalIndex] = guestBookMedias[originalIndex].copy(
                                        thumbnailUrl = state.urls.getOrNull(uploadedIndex)
                                    )
                                }
                            }

                            is UploadState.Failure -> throw Exception(state.message)
                            is UploadState.Cancelled -> throw CancellationException(UploadNoti.MSG_CANCELLED)
                            else -> {}
                        }
                    }
            } else {
                // 썸네일이 없는 경우 썸네일 업로드 단계 건너뛰기
            }

            // 4단계: 방명록 생성/수정 (80-100%)
            updateProgress(progress = 80, currentFileName = "방명록 처리 중...", currentOrder = 0, totalCount = 1)
            val result = if (editingGuestBookId != -1L) {
                guestBookRepository.updateGuestBook(
                    guestBookId = editingGuestBookId,
                    textContent = guestBookText,
                    // 유지할 기존 이미지 미디어의 ID 목록 (삭제되지 않고 계속 보존될 이미지들)
                    existingImageIds = guestBookMedias.filter { it.id != null && it.type == MediaType.IMAGE }
                        .map { it.id!! },
                    existingVideoIds = guestBookMedias.filter { it.id != null && it.type == MediaType.VIDEO }
                        .map { it.id!! },
                    existingAudioIds = guestBookMedias.filter { it.id != null && it.type == MediaType.AUDIO }
                        .map { it.id!! },
                    newMedias = guestBookMedias.filter { it.id == null }
                )

            } else {
                guestBookRepository.createGuestBook(
                    invitationId = invitationId,
                    textContent = guestBookText,
                    medias = guestBookMedias
                )
            }


            when (result) {
                is DomainResult.Success -> {
                    updateProgress(
                        progress = 100,
                        currentFileName = "완료",
                        currentOrder = guestBookMedias.size,
                        totalCount = guestBookMedias.size
                    )
                    notificationManager.notifyComplete(id)
                    Result.success(
                        workDataOf(
                            UploadKey.RESULT_URLS to guestBookMedias.map { it.url }.toTypedArray(),
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
        currentFileName: String? = null,
        currentOrder: Int? = null,
        totalCount: Int? = null,

        ) {
        setProgress(
            workDataOf(
                UploadKey.PROGRESS to progress,
                UploadKey.CURRENT_FILE_NAME to currentFileName,
                UploadKey.CURRENT_ORDER to currentOrder,
                UploadKey.TOTAL_COUNT to totalCount,

                )
        )

        notificationManager.update(
            id,
            notificationManager.createProgressNotification(
                workId = id,
                progress = progress,
                currentFileName = currentFileName,
                currentOrder = currentOrder,
                totalCount = totalCount
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


