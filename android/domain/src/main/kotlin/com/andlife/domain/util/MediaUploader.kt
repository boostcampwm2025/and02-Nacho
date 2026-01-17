package com.andlife.domain.util

import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.MediaFile

interface MediaUploader {
    suspend fun uploadMedias(files: List<MediaFile>): Result<List<String?>, DataError>
}
