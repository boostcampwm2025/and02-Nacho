package com.andlife.domain.util

import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.MediaFile
import com.andlife.domain.model.guestbook.UploadGuestBookState
import kotlinx.coroutines.flow.Flow

interface MediaUploader {
    suspend fun uploadMedias(files: List<MediaFile>): Result<List<String?>, DataError>
    fun uploadMediasWithProgress(files: List<MediaFile>): Flow<UploadGuestBookState>
}
