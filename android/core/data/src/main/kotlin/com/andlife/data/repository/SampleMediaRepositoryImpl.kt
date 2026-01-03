package com.andlife.data.repository

import com.andlife.data.util.media.MediaUploader
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.MediaType
import com.andlife.domain.util.Result
import com.andlife.domain.repository.SampleMediaRepository
import jakarta.inject.Inject
import java.io.File

class SampleMediaRepositoryImpl @Inject constructor(
    private val mediaUploader: MediaUploader
) : SampleMediaRepository {

    override suspend fun uploadSingleMedia(
        file: File,
        mediaType: MediaType
    ): Result<String, DataError> {
        return mediaUploader.uploadMedia(file, mediaType)
    }

    override suspend fun uploadMultipleMedia(
        files: List<Pair<File, MediaType>>
    ): Result<List<String?>, DataError> {
        return mediaUploader.uploadMediaBatch(files)
    }
}
