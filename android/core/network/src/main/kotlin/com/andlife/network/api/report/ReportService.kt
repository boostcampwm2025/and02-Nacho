package com.andlife.network.api.report

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.report.CreateReportRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("/api/reports")
    suspend fun sendReport(
        @Body request: CreateReportRequest,
    ): BaseResponse<Unit>
}
