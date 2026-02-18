package com.andlife.data.util.media.upload

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.andlife.domain.model.guestbook.UploadState
import com.andlife.domain.util.BackgroundMediaUploader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class BackgroundMediaUploaderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : BackgroundMediaUploader {
    private val workManager = WorkManager.getInstance(context)

    override fun uploadMediasInBackground(
        uriStrings: List<String>,
        thumbnailUrlStrings: List<String>,
        existingMediaTypes: List<String>,
        newMediaIndexs: List<Int>,
        invitationId: String,
        guestBookText: String,
        isEditing: Boolean,
        editingGuestBookId: String?
    ): String {
        val workData = workDataOf(
            UploadKey.MEDIA_URIS to uriStrings.toTypedArray(),
            UploadKey.THUMBNAIL_URLS to thumbnailUrlStrings.toTypedArray(),
            UploadKey.EXISTING_MEDIA_TYPES to existingMediaTypes.toTypedArray(),
            UploadKey.NEW_MEDIA_INDEXS to newMediaIndexs.toTypedArray(),
            UploadKey.INVITATION_ID to invitationId,
            UploadKey.GUEST_BOOK_TEXT to guestBookText,
            UploadKey.IS_EDITING to isEditing,
            UploadKey.EDITING_GUEST_BOOK_ID to editingGuestBookId
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(workData)
            .setConstraints(constraints)
            .addTag(UploadKey.TAG_MEDIA_UPLOAD)
            .build()

        workManager.enqueue(uploadRequest)

        return uploadRequest.id.toString()
    }

    override fun observeUploadProgress(workId: String): Flow<UploadState> {
        return workManager
            .getWorkInfoByIdFlow(UUID.fromString(workId))
            .map { workInfo ->
                if (workInfo == null) return@map UploadState.Enqueued

                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED ->
                        UploadState.Enqueued

                    WorkInfo.State.RUNNING -> {
                        val progress = workInfo.progress
                        UploadState.Progress(
                            percent = progress.getInt(UploadKey.PROGRESS, 0),
                            currentFileName = progress.getString(UploadKey.CURRENT_FILE_NAME),
                            currentOrder = progress.getInt(UploadKey.CURRENT_ORDER, 0),
                            totalCount = progress.getInt(UploadKey.TOTAL_COUNT, 1),
                        )
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        val urls =
                            workInfo.outputData.getStringArray(UploadKey.RESULT_URLS)
                                ?.toList()
                                ?: emptyList()

                        UploadState.Success(urls)
                    }

                    WorkInfo.State.FAILED -> {
                        val message =
                            workInfo.outputData.getString(UploadKey.ERROR_MESSAGE)
                                ?: UploadError.UNKNOWN

                        UploadState.Failure(message)
                    }

                    WorkInfo.State.CANCELLED ->
                        UploadState.Cancelled

                    else ->
                        UploadState.Enqueued
                }
            }
    }
    
    override fun cancelUpload(workId: String) {
        val uuid = UUID.fromString(workId)
        workManager.cancelWorkById(uuid)
    }
}
