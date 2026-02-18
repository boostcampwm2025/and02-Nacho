package com.andlife.domain.util

import com.andlife.domain.model.guestbook.UploadState
import kotlinx.coroutines.flow.Flow

interface BackgroundMediaUploader {
    /**
     * 미디어 파일들을 백그라운드에서 업로드하고 방명록을 생성/수정
     * @param invitationId 초대 ID
     * @param guestBookText 방명록 텍스트
     * @param editingGuestBookId 편집 중인 방명록 ID (null이면 새 방명록 생성)
     * @param selectedMediasId 선택된 미디어 ID들
     * @param selectedMediasUri 선택된 미디어 URI들
     * @param selectedMediasType 선택된 미디어 타입들
     * @param selectedMediasDuration 선택된 미디어 길이들 (영상인 경우)
     * @param selectedMediasThumbnailUrl 선택된 미디어 썸네일 URL들 (영상인 경우)
     * @return 업로드 작업 ID
     */
    fun uploadMediasInBackground(
        invitationId: Long,
        guestBookText: String,
        editingGuestBookId: Long?,
        selectedMediasId: String, // List<Long?>
        selectedMediasUri: String, // List<String>
        selectedMediasType: String, // List<String>
        selectedMediasDuration: String, // List<Int?>
        selectedMediasThumbnailUrl: String, // List<String?>
    ): String

    /**
     * 특정 업로드 작업의 진행 상황을 관찰
     * @param workId 작업 ID
     * @return UploadState를 담은 Flow
     */
    fun observeUploadProgress(workId: String): Flow<UploadState>

    /**
     * 업로드 작업을 취소
     * @param workId 취소할 작업 ID
     */
    fun cancelUpload(workId: String)
}
