package com.andlife.data.util.media.download

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.andlife.domain.model.guestbook.DownloadState
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.util.MediaDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MediaDownloaderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : MediaDownloader {
    private val workManager = WorkManager.getInstance(context)

    override fun enqueueDownload(
        url: String,
        fileName: String,
        mediaType: MediaType
    ): String {
        val inputData = workDataOf(
            DownloadKey.URL to url,
            DownloadKey.FILE_NAME to fileName,
            DownloadKey.MEDIA_TYPE to mediaType.ordinal
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(DownloadKey.TAG_MEDIA_DOWNLOAD)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueue(downloadRequest)

        return downloadRequest.id.toString()
    }

    override fun getDownloadStatus(workId: String): Flow<DownloadState> {
        val uuid: UUID = UUID.fromString(workId)
        return workManager.getWorkInfoByIdFlow(uuid)
            .map { workInfo -> workInfo.toDownloadState() }
    }

    override fun cancelDownload(workId: String) {
        val uuid: UUID = UUID.fromString(workId)
        workManager.cancelWorkById(uuid)
    }

    override fun cancelAllDownloads() {
        workManager.cancelAllWorkByTag(DownloadKey.TAG_MEDIA_DOWNLOAD)
    }
}

private fun WorkInfo?.toDownloadState(): DownloadState {
    if (this == null) return DownloadState.Idle

    return when (state) {
        WorkInfo.State.ENQUEUED -> DownloadState.Idle

        WorkInfo.State.RUNNING -> {
            val progressValue = progress.getInt(DownloadKey.PROGRESS, 0)
            DownloadState.Downloading(progressValue)
        }

        WorkInfo.State.SUCCEEDED -> {
            val resultUrl = outputData.getString(DownloadKey.RESULT_URL).orEmpty()
            DownloadState.Success(url = resultUrl)
        }

        WorkInfo.State.FAILED -> {
            val errorMsg = outputData.getString(DownloadKey.ERROR_MESSAGE) ?: DownloadError.UNKNOWN
            DownloadState.Error(message = errorMsg)
        }

        WorkInfo.State.CANCELLED -> {
            DownloadState.Error(message = DownloadNoti.MSG_CANCELLED)
        }

        else -> DownloadState.Idle
    }
}
