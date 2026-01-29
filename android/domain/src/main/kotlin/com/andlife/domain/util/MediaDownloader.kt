package com.andlife.domain.util

import com.andlife.domain.model.guestbook.DownloadState
import com.andlife.domain.model.guestbook.MediaType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface MediaDownloader {

    fun enqueueDownload(
        url: String,
        fileName: String,
        mediaType: MediaType,
    ): UUID

    fun getDownloadStatus(workId: UUID): Flow<DownloadState>

    fun cancelDownload(workId: UUID)

    fun cancelAllDownloads()
}
