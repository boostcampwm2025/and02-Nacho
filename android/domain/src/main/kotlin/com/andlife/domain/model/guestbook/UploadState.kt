package com.andlife.domain.model.guestbook

sealed interface UploadState {
    object Enqueued : UploadState
    data class Progress(
        val percent: Int,
        val currentIndex: Int,
        val totalFiles: Int,
        val currentFileName: String?
    ) : UploadState

    data class Success(val urls: List<String>) : UploadState
    data class Failure(val message: String) : UploadState
    
    object Cancelled : UploadState
}
