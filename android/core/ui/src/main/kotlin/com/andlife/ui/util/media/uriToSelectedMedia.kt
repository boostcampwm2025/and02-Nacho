package com.andlife.ui.util.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.andlife.ui.component.invitation.SelectedMedia
import com.andlife.ui.model.UiMediaType

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
            else -> UiMediaType.IMAGE
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
