package com.andlife.data.util.media

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
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
            DownloadWorker.KEY_URL to url,
            DownloadWorker.KEY_FILE_NAME to fileName,
            DownloadWorker.KEY_MEDIA_TYPE to mediaType.ordinal
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(DOWNLOAD_WORK_TAG)
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
        workManager.cancelAllWorkByTag(DOWNLOAD_WORK_TAG)
    }

    companion object {
        const val DOWNLOAD_WORK_TAG = "media_download"
    }
}

private fun WorkInfo?.toDownloadState(): DownloadState {
    if (this == null) return DownloadState.Idle

    return when (state) {
        WorkInfo.State.ENQUEUED -> DownloadState.Idle

        WorkInfo.State.RUNNING -> {
            val progressValue = progress.getInt(DownloadWorker.KEY_PROGRESS, 0)
            DownloadState.Downloading(progressValue)
        }

        WorkInfo.State.SUCCEEDED -> {
            val resultUrl = outputData.getString(DownloadWorker.KEY_RESULT_URL) ?: ""
            DownloadState.Success(url = resultUrl)
        }

        WorkInfo.State.FAILED -> {
            val errorMsg = outputData.getString(DownloadWorker.KEY_ERROR_MESSAGE) ?: "알 수 없는 오류"
            DownloadState.Error(message = errorMsg)
        }

        WorkInfo.State.CANCELLED -> {
            DownloadState.Error(message = "다운로드가 취소되었습니다.")
        }

        else -> DownloadState.Idle
    }
}
