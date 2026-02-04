package com.andlife.domain.util

import com.andlife.domain.model.guestbook.UploadState
import kotlinx.coroutines.flow.Flow

interface BackgroundMediaUploader {
    /**
     * 미디어 파일들을 백그라운드에서 업로드
     * @param uriStrings 업로드할 파일들의 URI 문자열 리스트
     * @return 업로드 작업의 ID
     */
    fun uploadMediasInBackground(uriStrings: List<String>): String

    /**
     * 특정 업로드 작업의 진행 상황을 관찰
     * @param workId 작업 ID
     * @return WorkInfo를 담은 Flow
     */
    fun observeUploadProgress(workId: String): Flow<UploadState>

    /**
     * 업로드 작업을 취소
     * @param workId 취소할 작업 ID
     */
    fun cancelUpload(workId: String)
}
