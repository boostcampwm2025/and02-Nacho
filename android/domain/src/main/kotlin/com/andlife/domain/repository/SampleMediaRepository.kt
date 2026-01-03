package com.andlife.domain.repository

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import java.io.File

interface SampleMediaRepository {
    suspend fun uploadSingleMedia(
        file: File,
        mediaType: MediaType
    ): Result<String, DataError>

    suspend fun uploadMultipleMedia(
        files: List<Pair<File, MediaType>>
    ): Result<List<String?>, DataError>
}

enum class MediaType(val contentType: String) {
    IMAGE("image/jpeg"),
    VIDEO("video/mp4"),
    AUDIO("audio/mpeg")
}
