package com.andlife.ui.util.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.andlife.ui.component.invitation.SelectedMedia
import com.andlife.model.guestbook.UiMediaType

// URI 문자열을 SelectedMedia 객체로 변환하는 유틸 함수
fun uriToSelectedMedia(
    context: Context,
    uriString: String,
): SelectedMedia {
    val uri = Uri.parse(uriString)

    val mimeType =
        try {
            context.contentResolver.getType(uri)
        } catch (e: Exception) {
            null
        }

    val mediaType =
        when {
            mimeType?.startsWith("image/") == true -> UiMediaType.IMAGE
            mimeType?.startsWith("video/") == true -> UiMediaType.VIDEO
            mimeType?.startsWith("audio/") == true -> UiMediaType.AUDIO
            else -> {
                // 파일 확장자로 판별(file uri용)
                val path = uri.path ?: uriString.lowercase()
                when {
                    path.endsWith(".m4a") ||
                        path.endsWith(".mp3") ||
                        path.endsWith(".aac") ||
                        path.endsWith(".wav") ||
                        path.endsWith(".3gp") -> UiMediaType.AUDIO

                    path.endsWith(".mp4") ||
                        path.endsWith(".mov") ||
                        path.endsWith(".avi") ||
                        path.endsWith(".mkv") -> UiMediaType.VIDEO

                    path.endsWith(".jpg") ||
                        path.endsWith(".jpeg") ||
                        path.endsWith(".png") ||
                        path.endsWith(".gif") ||
                        path.endsWith(".webp") -> UiMediaType.IMAGE

                    else -> UiMediaType.IMAGE // 기본값
                }
            }
        }

    val duration =
        if (mediaType == UiMediaType.VIDEO || mediaType == UiMediaType.AUDIO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)
                retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?.div(1000)
                    ?.toInt()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

    return SelectedMedia(
        uri = uriString,
        type = mediaType,
        duration = duration,
    )
}
