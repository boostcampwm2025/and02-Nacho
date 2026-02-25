package com.andlife.ui.util.media

import android.content.Context
import android.net.Uri
import com.andlife.ui.component.invitation.SelectedMedia

/**
 * 선택된 미디어(SelectedMedia) 목록을 순서대로 검사하여 업로드 가능한 항목만 선별합니다.
 *
 * 검사 규칙
 * 1. 미디어 목록 순서대로 검사합니다.
 * 2. 최대 선택 가능 개수(availableSlotCnt)를 초과하면 이후 미디어는 검사하지 않습니다.
 * 3. 용량 합이 500MB를 초과하면, 해당 미디어는 제외하고 다음 미디어를 계속 검사합니다.
 *
 * 반환값:
 * - 첫 번째 값: 업로드 가능한 미디어 목록
 * - 두 번째 값: 용량 초과로 제외된 미디어가 존재하는지 여부
 * - 세 번째 값: 선택 가능 개수 초과로 인해 검사가 종료되었는지 여부
 */

private const val MAX_MEDIAS_COUNT = 20
private const val MAX_MEDIA_SIZE_BYTES = 500 * 1024 * 1024L // 500MB

fun validateSelectedMediasByRule(
    selectedMedias: List<SelectedMedia>,
): Triple<List<SelectedMedia>, Boolean, Boolean> {
    val validSelectedMedias = mutableListOf<SelectedMedia>()
    var exceededAvailableBytes = false
    var exceededAvailableSlots = false
    var currentBytes = 0L

    selectedMedias
        .forEach { selectedMedia ->
            val fileSize = selectedMedia.sizeBytes

            if (fileSize != null) {
                if (validSelectedMedias.size > MAX_MEDIAS_COUNT) {
                    exceededAvailableSlots = true
                    return@forEach
                } else if (currentBytes + fileSize > MAX_MEDIA_SIZE_BYTES) {
                    exceededAvailableBytes = true
                } else {
                    validSelectedMedias.add(selectedMedia)
                    currentBytes += fileSize
                }
            } else {
                // TODO: 파일을 읽을 수 없는 경우 별도의 스낵바 안내 필요
            }
        }
    return Triple(validSelectedMedias, exceededAvailableBytes, exceededAvailableSlots)
}

fun getFileSizeOrNull(
    context: Context,
    uri: Uri,
): Long? {
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            pfd.statSize.takeIf { it > 0 }
        }
    } catch (e: Exception) {
        null
    }
}

