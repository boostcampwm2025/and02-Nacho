package com.andlife.nachoserver.request.report

import com.andlife.nachoserver.constant.ReportReason
import com.andlife.nachoserver.constant.ReportTargetType

data class CreateReportRequest(
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: ReportReason,
    val description: String?
)