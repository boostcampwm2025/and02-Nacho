package com.andlife.domain.model.guestbook

sealed interface UploadState {
    data object Enqueued : UploadState
    data class Progress(
        val percent: Int,
        val currentFileName: String? = null,
        val currentOrder: Int? = null,
        val totalCount: Int? = null,
    ) : UploadState

    data class Success(val urls: List<String?>) : UploadState
    data class Failure(val message: String) : UploadState

    data object Cancelled : UploadState
}
