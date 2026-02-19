package com.andlife.nachoserver.request.report

import com.andlife.nachoserver.constant.ReportTargetType

data class CreateReportRequest(
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: String,
    val description: String?
)