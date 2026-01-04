package com.andlife.data.repository

import com.andlife.data.util.media.MediaUploader
import com.andlife.domain.error.DataError
import com.andlife.domain.model.MediaFile
import com.andlife.domain.repository.SampleMediaRepository
import com.andlife.domain.util.Result
import jakarta.inject.Inject

class SampleMediaRepositoryImpl @Inject constructor(
    private val mediaUploader: MediaUploader
) : SampleMediaRepository {

    override suspend fun uploadMedia(
        files: List<MediaFile>
    ): Result<List<String?>, DataError> {
        return mediaUploader.uploadMedias(files)
    }
}
