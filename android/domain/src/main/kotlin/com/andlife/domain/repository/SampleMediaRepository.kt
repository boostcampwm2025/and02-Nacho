package com.andlife.domain.repository

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result

interface SampleMediaRepository {
    suspend fun uploadMedias(
        uriStrings: List<String>
    ): Result<List<String?>, DataError>
}
