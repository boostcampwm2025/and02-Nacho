package com.andlife.data.datasource.remote.report

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.report.CreateReportRequest

interface ReportRemoteDataSource {
    suspend fun sendReport(request: CreateReportRequest): Result<Unit, DataError>
}
