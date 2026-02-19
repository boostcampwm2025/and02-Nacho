package com.andlife.domain.repository.report

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result

interface ReportRepository {
    suspend fun sendReport(
        targetType: String,
        targetId: Long,
        reason: String,
        description: String?,
    ): Result<Unit, DataError>
}
