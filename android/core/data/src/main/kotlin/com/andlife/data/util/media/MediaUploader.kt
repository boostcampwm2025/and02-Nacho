package com.andlife.data.util.media

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result

interface MediaUploader {

    suspend fun uploadMedias(files: List<MediaFile>): Result<List<String?>, DataError>
}
