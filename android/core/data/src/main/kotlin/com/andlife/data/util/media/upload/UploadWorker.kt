package com.andlife.data.util.media.upload

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
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
        // 썸네일 URL 리스트 가져오기
        val thumbnailUrlStrings = inputData.getStringArray(UploadKey.THUMBNAIL_URLS)?.toList() ?: emptyList()
        // 기존 미디어 타입 리스트 가져오기
        val existingMediaTypes = inputData
            .getStringArray(UploadKey.EXISTING_MEDIA_TYPES)
            ?.map { MediaType.valueOf(it) }
            ?: emptyList()
        // 신규 미디어 여부 리스트 가져오기
        val newMediaIndexs = inputData.getIntArray(UploadKey.NEW_MEDIA_INDEXS)
        val newMediaIndexSet = newMediaIndexs?.toSet() ?: emptySet()

        val workMedias = uriStrings.mapIndexed { index, uri ->

            val isNew = index in newMediaIndexSet

            val mediaType = if (isNew) {
                context.contentResolver
                    .getType(uri.toUri())
                    ?.let { type ->
                        when {
                            type.startsWith("image/") -> MediaType.IMAGE
                            type.startsWith("video/") -> MediaType.VIDEO
                            type.startsWith("audio/") -> MediaType.AUDIO
                            else -> null
                        }
                    }
                    ?: throw IllegalArgumentException("지원하지 않는 로컬 미디어 타입: $uri")
            } else {
                existingMediaTypes[index]
            }

            WorkMedia(
                uri = uri,
                existingThumbnailUrl = thumbnailUrlStrings.getOrNull(index),
                isNew = isNew,
                mediaType = mediaType
            )
        }


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

            // 새로운 미디어에 대해 MediaFile 객체 생성
            val newWorkMedias = workMedias.filter { it.isNew }

            // 1단계: 미디어 업로드 (0-60%)

            if (newWorkMedias.isNotEmpty()) {
                val mediaFiles = mediaFileProvider.createFromUris(
                    newWorkMedias.map { it.uri }
                )

                newWorkMedias.forEachIndexed { index, workMedia ->
                    workMedia.mediaFile = mediaFiles.getOrNull(index)
                }

                mediaUploader.uploadMediasWithProgress(mediaFiles)
                    .collect { state ->
                        when (state) {
                            is UploadState.Progress -> {
                                updateProgress(
                                    progress = (state.percent * 0.6).toInt(),
                                    currentIndex = state.currentIndex,
                                    totalFiles = state.totalFiles,
                                    currentFileName = state.currentFileName
                                )
                            }

                            is UploadState.Success -> {
                                state.urls.forEachIndexed { index, url ->
                                    newWorkMedias.getOrNull(index)?.uploadedUrl = url
                                }
                            }

                            is UploadState.Failure -> throw Exception(state.message)
                            is UploadState.Cancelled -> throw CancellationException(UploadNoti.MSG_CANCELLED)
                            else -> {}
                        }
                    }
            }

            // 2단계: 썸네일 생성 (60-70%)
            val videoWorkMedias = newWorkMedias.filter {
                it.mediaType == MediaType.VIDEO
            }

            videoWorkMedias.forEachIndexed { index, workMedia ->
                val thumbnailFile = thumbnailGenerator.generateVideoThumbnail(workMedia.uri)
                if (thumbnailFile != null) {
                    workMedia.thumbnailFile = mediaFileProvider.createFromFile(thumbnailFile)
                }

                updateProgress(
                    progress = 60 + ((index + 1) * 10 / videoWorkMedias.size),
                    currentIndex = index + 1,
                    totalFiles = videoWorkMedias.size,
                    currentFileName = "썸네일 생성 중"
                )
            }

            // 3단계: 썸네일 업로드 (70-80%)
            val thumbnailTargets = videoWorkMedias.filter { it.thumbnailFile != null }
            val thumbnailFiles = thumbnailTargets.mapNotNull { it.thumbnailFile }

            if (thumbnailFiles.isNotEmpty()) {
                mediaUploader.uploadMediasWithProgress(thumbnailFiles)
                    .collect { state ->
                        when (state) {
                            is UploadState.Progress -> {
                                updateProgress(
                                    progress = 70 + (state.percent * 0.1).toInt(),
                                    currentIndex = state.currentIndex,
                                    totalFiles = state.totalFiles,
                                    currentFileName = state.currentFileName
                                )
                            }

                            is UploadState.Success -> {
                                state.urls.forEachIndexed { index, url ->
                                    thumbnailTargets.getOrNull(index)?.uploadedThumbnailUrl = url
                                }
                            }

                            is UploadState.Failure -> throw Exception(state.message)
                            is UploadState.Cancelled -> throw CancellationException(UploadNoti.MSG_CANCELLED)
                            else -> {}
                        }
                    }
            }

            // 4단계: GuestBookMedia 리스트 생성 (80-85%)
            val guestBookMediaList = workMedias.mapIndexed { displayOrder, workMedia ->
                if (workMedia.isNew) {
                    GuestBookMedia(
                        id = 0,
                        type = workMedia.mediaType,
                        url = workMedia.uploadedUrl ?: "",
                        thumbnailUrl = if (workMedia.mediaType == MediaType.VIDEO)
                            workMedia.uploadedThumbnailUrl
                        else null,
                        durationSeconds = null,
                        displayOrder = displayOrder
                    )
                } else {
                    GuestBookMedia(
                        id = 0,
                        type = workMedia.mediaType,
                        url = workMedia.uri,
                        thumbnailUrl = workMedia.existingThumbnailUrl,
                        durationSeconds = null,
                        displayOrder = displayOrder
                    )
                }
            }


            // 5단계: 방명록 생성/수정 (85-100%)
            updateProgress(progress = 85, currentIndex = 0, totalFiles = 1, currentFileName = "방명록 처리 중...")
            val result = if (isEditing && editingGuestBookId != null) {

                val newMedias = guestBookMediaList.filter {
                    workMedias[it.displayOrder].isNew
                }

                val existingMedias = guestBookMediaList.filter {
                    !workMedias[it.displayOrder].isNew
                }

                guestBookRepository.updateGuestBook(
                    guestBookId = editingGuestBookId,
                    textContent = guestBookText,
                    existingImageIds = existingMedias.mapNotNull { media ->
                        // 기존 미디어의 ID는 URI에 담겨옴
                        media.url.toLongOrNull()
                    },
                    existingAudioIds = existingMedias.filter { it.type == MediaType.AUDIO }
                        .mapNotNull { media ->
                            media.url.toLongOrNull()
                        },
                    existingVideoIds = existingMedias.filter { it.type == MediaType.VIDEO }
                        .mapNotNull { media ->
                            media.url.toLongOrNull()
                        },
                    newMedias = newMedias
                )

            } else {
                guestBookRepository.createGuestBook(
                    invitationId = invitationId,
                    textContent = guestBookText,
                    medias = guestBookMediaList
                )
            }


            when (result) {
                is DomainResult.Success -> {
                    updateProgress(progress = 100, currentIndex = 1, totalFiles = 1, currentFileName = "완료")
                    notificationManager.notifyComplete(id)
                    Result.success(
                        workDataOf(
                            UploadKey.RESULT_URLS to guestBookMediaList.map { it.url }.toTypedArray(),
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

private data class WorkMedia(
    val uri: String,
    val existingThumbnailUrl: String?,
    val isNew: Boolean,
    val mediaType: MediaType,
    var mediaFile: com.andlife.domain.model.guestbook.MediaFile? = null,
    var uploadedUrl: String? = null,
    var thumbnailFile: com.andlife.domain.model.guestbook.MediaFile? = null,
    var uploadedThumbnailUrl: String? = null
)
