package com.andlife.data.util.media.upload

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.andlife.domain.model.guestbook.UploadGuestBookState
import com.andlife.domain.util.BackgroundMediaUploader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID
import javax.inject.Inject

class BackgroundMediaUploaderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : BackgroundMediaUploader {
    private val workManager = WorkManager.getInstance(context)

    override fun uploadMediasInBackground(
        invitationId: Long,
        guestBookText: String,
        editingGuestBookId: Long,
        selectedMediasId: String, // List<Long?>
        selectedMediasUri: String, // List<String>
        selectedMediasType: String, // List<String>
        selectedMediasDuration: String, // List<Int?>
        selectedMediasThumbnailUrl: String, // List<String?>
    ): Pair<String, String> {
        val workData = workDataOf(
            UploadKey.INVITATION_ID to invitationId,
            UploadKey.GUEST_BOOK_TEXT to guestBookText,
            UploadKey.EDITING_GUEST_BOOK_ID to editingGuestBookId,
            UploadKey.MEDIA_IDS to selectedMediasId,
            UploadKey.MEDIA_URIS to selectedMediasUri,
            UploadKey.MEDIA_TYPES to selectedMediasType,
            UploadKey.MEDIA_DURATIONS to selectedMediasDuration,
            UploadKey.MEDIA_THUMBNAIL_URLS to selectedMediasThumbnailUrl,
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(workData)
            .setConstraints(constraints)
            .addTag(UploadKey.TAG_MEDIA_UPLOAD)
            .build()

        val guestBookRequest = OneTimeWorkRequestBuilder<GuestBookWorker>()
            .setConstraints(constraints)
            .addTag(UploadKey.TAG_MEDIA_UPLOAD)
            .build()

        workManager
            .beginWith(uploadRequest)
            .then(guestBookRequest)
            .enqueue()

        return uploadRequest.id.toString() to guestBookRequest.id.toString()
    }

    override fun observeUploadProgress(pairOfWorkIds: Pair<String, String>): Flow<UploadGuestBookState> {
        val uploadWorkId = pairOfWorkIds.first
        val guestBookWorkId = pairOfWorkIds.second

        return combine(
            workManager.getWorkInfoByIdFlow(UUID.fromString(uploadWorkId)),
            workManager.getWorkInfoByIdFlow(UUID.fromString(guestBookWorkId))
        ) { uploadInfo, guestBookInfo ->
            when {
                // 업로드 작업이 실행 중
                uploadInfo?.state == WorkInfo.State.RUNNING -> {
                    val progress = uploadInfo.progress
                    UploadGuestBookState.Progress(
                        percent = progress.getInt(UploadKey.PROGRESS, 0),
                        currentFileName = progress.getString(UploadKey.CURRENT_FILE_NAME),
                        currentOrder = progress.getInt(UploadKey.CURRENT_ORDER, 0),
                        totalCount = progress.getInt(UploadKey.TOTAL_COUNT, 1),
                    )
                }

                // 업로드 완료, 방명록 작업 대기 중
                uploadInfo?.state == WorkInfo.State.SUCCEEDED &&
                    guestBookInfo?.state == WorkInfo.State.ENQUEUED -> {
                    UploadGuestBookState.Progress(
                        percent = 85,
                        currentFileName = "방명록 처리 준비 중...",
                        currentOrder = 1,
                        totalCount = 1,
                    )
                }

                // 업로드 완료, 방명록 처리 중
                uploadInfo?.state == WorkInfo.State.SUCCEEDED &&
                    guestBookInfo?.state == WorkInfo.State.RUNNING -> {
                    UploadGuestBookState.Progress(
                        percent = 90,
                        currentFileName = "방명록 처리 중...",
                        currentOrder = 1,
                        totalCount = 1,
                    )
                }

                // 전체 완료 - GuestBookWorker에서 최종 결과 반환
                guestBookInfo?.state == WorkInfo.State.SUCCEEDED -> {
                    val urls = guestBookInfo.outputData.getStringArray(UploadKey.RESULT_URLS)
                        ?.toList() ?: emptyList()
                    UploadGuestBookState.Success(urls)
                }

                // 업로드 작업 실패
                uploadInfo?.state == WorkInfo.State.FAILED -> {
                    val message = uploadInfo.outputData.getString(UploadKey.ERROR_MESSAGE)
                        ?: UploadError.UNKNOWN
                    UploadGuestBookState.Failure("업로드 실패: $message")
                }

                // 방명록 처리 실패
                guestBookInfo?.state == WorkInfo.State.FAILED -> {
                    val message = guestBookInfo.outputData.getString(UploadKey.ERROR_MESSAGE)
                        ?: UploadError.UNKNOWN
                    UploadGuestBookState.Failure("방명록 처리 실패: $message")
                }

                // 취소됨
                uploadInfo?.state == WorkInfo.State.CANCELLED ||
                    guestBookInfo?.state == WorkInfo.State.CANCELLED -> {
                    UploadGuestBookState.Cancelled
                }

                else -> UploadGuestBookState.Enqueued
            }
        }
    }

    override fun cancelUpload(pairOfWorkIds: Pair<String, String>) {
        val uploadWorkId = pairOfWorkIds.first
        val guestBookWorkId = pairOfWorkIds.second
        workManager.cancelWorkById(UUID.fromString(uploadWorkId))
        workManager.cancelWorkById(UUID.fromString(guestBookWorkId))
    }
}
