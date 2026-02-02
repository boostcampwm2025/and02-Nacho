package com.andlife.ui.util.media

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri

private const val MAX_VIDEO_SIZE_BYTES = 200 * 1024 * 1024L // 200MB

fun validateUriStringsByFileSize(
    context: Context,
    uriStrings: List<String>,
    availableSlotsCnt: Int,
): Pair<List<String>, List<String>> {
    if (availableSlotsCnt <= 0) {
        return emptyList<String>() to uriStrings
    }

    val validUriStrings = mutableListOf<String>()
    val rejectedUriStrings = mutableListOf<String>()

    uriStrings
        .take(availableSlotsCnt)
        .forEach { uriString ->
            val uri = uriString.toUri()
            val fileSize = getFileSizeOrNull(context, uri)

            if (fileSize == null) {  // TODO: 파일을 읽을 수 없는 경우 별도의 스낵바 안내 필요
                rejectedUriStrings.add(uriString)
            } else if (fileSize > MAX_VIDEO_SIZE_BYTES) {
                rejectedUriStrings.add(uriString)
            } else {
                validUriStrings.add(uriString)
            }
        }

    return validUriStrings to rejectedUriStrings
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

