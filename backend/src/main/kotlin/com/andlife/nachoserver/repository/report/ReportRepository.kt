package com.andlife.nachoserver.repository.report

import com.andlife.nachoserver.constant.ReportTargetType
import com.andlife.nachoserver.entity.Report
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<Report, Long> {
    fun existsByReporterIdAndTargetTypeAndTargetId(
        reporterId: Long,
        targetType: ReportTargetType,
        targetId: Long
    ): Boolean
}