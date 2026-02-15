package com.andlife.data.datasource.remote.report

import com.andlife.data.util.apiCall
import com.andlife.network.api.report.ReportService
import com.andlife.network.model.report.CreateReportRequest
import javax.inject.Inject

class ReportRemoteDataSourceImpl @Inject constructor(
    private val reportService: ReportService
) : ReportRemoteDataSource {
    override suspend fun sendReport(request: CreateReportRequest) =
        apiCall { reportService.sendReport(request) }
}
