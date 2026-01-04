package com.andlife.domain.repository

import com.andlife.domain.error.DataError
import com.andlife.domain.model.MediaType
import com.andlife.domain.util.Result
import java.io.File

interface SampleMediaRepository {

    suspend fun uploadMedias(
        files: List<Pair<File, MediaType>>
    ): Result<List<String?>, DataError>
}
