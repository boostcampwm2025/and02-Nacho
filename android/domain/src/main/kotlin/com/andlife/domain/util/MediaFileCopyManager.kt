package com.andlife.domain.util

/**
 * Content URI 파일을 앱 내부 저장소로 복사합니다.
 */
interface MediaFileCopyManager {
    suspend fun copyPhotoPickerFileToInternal(uriString: String): String?

    suspend fun copyFilesToInternal(uriStrings: List<String>): List<String?>

    // 오래된 복사 파일들 정리 (메모리 관리)
    fun cleanupTempFiles(olderThanHours: Int = 24)
}
