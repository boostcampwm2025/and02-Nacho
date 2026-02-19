package com.andlife.data.repository.report

import com.andlife.data.datasource.remote.report.ReportRemoteDataSource
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.report.ReportRepository
import com.andlife.domain.util.Result
import com.andlife.network.model.report.CreateReportRequest
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportRemoteDataSource: ReportRemoteDataSource
) : ReportRepository {
    override suspend fun sendReport(
        targetType: String,
        targetId: Long,
        reason: String,
        description: String?,
    ): Result<Unit, DataError> {
        val request = CreateReportRequest(
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            description = description
        )
        return reportRemoteDataSource.sendReport(request)
    }
}
