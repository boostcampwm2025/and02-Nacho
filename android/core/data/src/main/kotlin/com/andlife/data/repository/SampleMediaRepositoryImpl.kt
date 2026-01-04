package com.andlife.data.repository

import com.andlife.data.util.media.MediaFile
import com.andlife.data.util.media.MediaUploader
import com.andlife.domain.error.DataError
import com.andlife.domain.model.MediaType
import com.andlife.domain.repository.SampleMediaRepository
import com.andlife.domain.util.Result
import jakarta.inject.Inject
import java.io.File

class SampleMediaRepositoryImpl @Inject constructor(
    private val mediaUploader: MediaUploader
) : SampleMediaRepository {

    override suspend fun uploadMedias(
        files: List<Pair<File, MediaType>>
    ): Result<List<String?>, DataError> {
        val mediaFiles = files.map { (file, type) ->
            MediaFile(file = file, mediaType = type)
        }

        return mediaUploader.uploadMedias(mediaFiles)
    }
}
