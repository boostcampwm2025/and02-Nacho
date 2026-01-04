package com.andlife.domain.repository

import com.andlife.domain.error.DataError
import com.andlife.domain.model.MediaFile
import com.andlife.domain.util.Result

interface SampleMediaRepository {
    suspend fun uploadMedia(
        files: List<MediaFile>
    ): Result<List<String?>, DataError>
}
