package com.andlife.domain.model.guestbook

sealed interface DownloadState {
    data object Idle: DownloadState
    data class Downloading(val progress: Int): DownloadState
    data class Success(val url: String): DownloadState
    data class Error(val message: String): DownloadState
}
