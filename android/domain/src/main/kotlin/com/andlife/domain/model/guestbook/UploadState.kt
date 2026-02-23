package com.andlife.domain.model.guestbook

/**
 * 방명록 미디어 업로드 및 방명록 생성/수정 작업의 상태
 */
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
