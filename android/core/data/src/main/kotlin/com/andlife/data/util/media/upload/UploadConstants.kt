package com.andlife.data.util.media.upload

object UploadKey {
    const val MEDIA_FILES = "media_files"

    const val INVITATION_ID = "invitation_id"
    const val GUEST_BOOK_TEXT = "guest_book_text"
    const val EDITING_GUEST_BOOK_ID = "editing_guest_book_id"

    const val MEDIA_IDS = "media_ids"
    const val MEDIA_URIS = "media_uris"
    const val MEDIA_TYPES = "media_types"
    const val MEDIA_DURATIONS = "media_durations"
    const val MEDIA_THUMBNAIL_URLS = "media_thumbnail_urls"
    
    const val RESULT_URLS = "result_urls"
    const val ERROR_MESSAGE = "error_message"
    const val PROGRESS = "progress"

    const val CURRENT_FILE_NAME = "current_file_name"
    const val CURRENT_ORDER = "current_order"
    const val TOTAL_COUNT = "total_count"
    const val EXTRA_WORK_ID = "extra_work_id"

    const val TAG_MEDIA_UPLOAD = "tag_media_upload"
}

object UploadNoti {
    const val CHANNEL_ID_PROGRESS = "upload_progress_channel"
    const val CHANNEL_ID_COMPLETE = "upload_complete_channel"
    const val CHANNEL_NAME_PROGRESS = "업로드 진행 상태"
    const val CHANNEL_NAME_COMPLETE = "업로드 완료 안내"

    const val TITLE_UPLOADING = "업로드 중"
    const val MSG_PREPARING = "파일을 업로드하고 있습니다."
    const val TITLE_COMPLETE = "업로드 완료"
    const val MSG_COMPLETE = "모든 파일이 성공적으로 업로드되었습니다."
    const val ACTION_CANCEL = "취소"
    const val MSG_CANCELLED = "업로드가 취소되었습니다."
}

object UploadError {
    const val MISSING_MEDIA_FILES = "미디어 파일이 없습니다"
    const val FILE_TOO_LARGE = "파일 크기가 제한을 초과합니다"
    const val UPLOAD_FAILED = "업로드 실패: "
    const val UNKNOWN = "알 수 없는 오류"
}
