package com.andlife.data.util.media

import com.andlife.domain.error.DataError
import com.andlife.domain.model.MediaType
import com.andlife.domain.util.Result
import java.io.File

interface MediaUploader {

    suspend fun uploadMedia(
        file: File,
        mediaType: MediaType
    ): Result<String, DataError>

    suspend fun uploadMediaBatch(
        files: List<Pair<File, MediaType>> // TODO: Pair -> Data Class
    ): Result<List<String?>, DataError>
}

