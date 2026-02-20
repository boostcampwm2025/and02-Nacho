package com.andlife.domain.util

/**
 * Photo Picker URI 파일을 앱 내부 저장소로 복사합니다.
 * 백그라운드 업로드 시 권한 문제를 해결하기 위해 사용합니다.
 */
interface MediaFileCopyManager {
    suspend fun copyPhotoPickerFileToInternal(uriString: String): String?

    suspend fun copyFilesToInternal(uriStrings: List<String>): List<String?>

    // 오래된 복사 파일들 정리 (메모리 관리)
    fun cleanupTempFiles(olderThanHours: Int = 24)
}
