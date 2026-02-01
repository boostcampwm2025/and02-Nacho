package com.andlife.data.util.media.download

import android.webkit.MimeTypeMap
import com.andlife.domain.model.guestbook.MediaType

internal fun resolveMimeType(fileName: String, mediaType: MediaType): String {
    val extension = fileName.substringAfterLast(".", "")
    val mimeType = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension.lowercase())

    return mimeType ?: when (mediaType) {
        MediaType.IMAGE -> DownloadFile.MIME_IMAGE
        MediaType.VIDEO -> DownloadFile.MIME_VIDEO
        MediaType.AUDIO -> DownloadFile.MIME_AUDIO
    }
}

object DownloadKey {
    const val URL = "url"
    const val FILE_NAME = "file_name"
    const val MEDIA_TYPE = "media_type"
    const val RESULT_URL = "result_url"
    const val ERROR_MESSAGE = "error_message"
    const val PROGRESS = "progress"
    const val EXTRA_WORK_ID = "extra_work_id"

    const val TAG_MEDIA_DOWNLOAD = "tag_media_download"
}

object DownloadNoti {
    const val CHANNEL_ID_PROGRESS = "download_progress_channel"
    const val CHANNEL_ID_COMPLETE = "download_complete_channel"
    const val CHANNEL_NAME_PROGRESS = "다운로드 진행 상태"
    const val CHANNEL_NAME_COMPLETE = "다운로드 완료 안내"

    const val ID_COMPLETE_VISUAL = 1001
    const val ID_COMPLETE_AUDIO = 1002

    const val TITLE_DOWNLOADING = "다운로드 중"
    const val MSG_PREPARING = "파일을 저장하고 있습니다."
    const val TITLE_COMPLETE_AUDIO = "음성 메시지 저장 완료"
    const val TITLE_COMPLETE_VISUAL = "미디어 저장 완료"
    const val MSG_COMPLETE_SUFFIX = "개의 파일이 저장되었습니다."
    const val ACTION_CANCEL = "취소"

    const val MSG_CANCELLED = "다운로드가 취소되었습니다."
}

object DownloadFile {
    const val SAVE_DIRECTORY_NAME = "나에게로의 초대"
    const val BUFFER_SIZE = 8 * 1024

    const val MIME_IMAGE = "image/*"
    const val MIME_VIDEO = "video/*"
    const val MIME_AUDIO = "audio/*"
    const val MIME_FOLDER_Q = "vnd.android.document/root"
    const val URI_STORAGE_ROOT = "content://com.android.externalstorage.documents/root/primary"

    const val PREF_NAME = "download_prefs"
    const val KEY_COUNT_AUDIO = "audio_count"
    const val KEY_COUNT_VISUAL = "visual_count"
}

object DownloadError {
    const val MISSING_URL = "URL이 없습니다"
    const val MISSING_FILE_NAME = "파일명이 없습니다"
    const val MISSING_MEDIA_TYPE = "미디어 타입이 없습니다"
    const val MISSING_BODY = "응답 본문이 없습니다."
    const val FAILED = "다운로드 실패: "
    const val UNKNOWN = "알 수 없는 오류"
}
