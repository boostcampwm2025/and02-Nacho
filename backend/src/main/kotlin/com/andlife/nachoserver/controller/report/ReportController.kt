package com.andlife.nachoserver.controller.report

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.request.report.CreateReportRequest
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.response.report.CreateReportResponse
import com.andlife.nachoserver.service.report.ReportService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService
) {
    @PostMapping
    fun createReport(
        authContext: AuthContext,
        @RequestBody request: CreateReportRequest
    ): BaseResponse<CreateReportResponse> {
        val result = reportService.createReport(authContext, request)
        return BaseResponse.success(result)
    }
}