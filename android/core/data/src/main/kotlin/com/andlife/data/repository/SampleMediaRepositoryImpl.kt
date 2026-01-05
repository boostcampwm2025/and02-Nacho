package com.andlife.data.repository

import com.andlife.data.util.media.MediaFileProvider
import com.andlife.data.util.media.MediaUploader
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.SampleMediaRepository
import com.andlife.domain.util.Result
import jakarta.inject.Inject

class SampleMediaRepositoryImpl @Inject constructor(
    private val mediaUploader: MediaUploader,
    private val mediaFileProvider: MediaFileProvider
) : SampleMediaRepository {

    override suspend fun uploadMedias(
        uriStrings: List<String>
    ): Result<List<String?>, DataError> {
        val mediaFiles = mediaFileProvider.createFromUris(uriStrings)

        if (mediaFiles.isEmpty()) {
            return Result.Error(DataError.Network.NOT_FOUND) // TODO: 적절한 에러로 변경, 이건 Network가 아니라 Local인가?
        }

        return mediaUploader.uploadMedias(mediaFiles)
    }
}
