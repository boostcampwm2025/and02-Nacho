package com.andlife.domain.util

import com.andlife.domain.model.guestbook.DownloadState
import com.andlife.domain.model.guestbook.MediaType
import kotlinx.coroutines.flow.Flow

interface MediaDownloader {

    fun enqueueDownload(
        url: String,
        fileName: String,
        mediaType: MediaType,
    ): String

    fun getDownloadStatus(workId: String): Flow<DownloadState>

    fun cancelDownload(workId: String)

    fun cancelAllDownloads()
}
