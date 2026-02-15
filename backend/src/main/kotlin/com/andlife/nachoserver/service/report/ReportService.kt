package com.andlife.nachoserver.service.report

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.constant.ReportReason
import com.andlife.nachoserver.constant.ReportTargetType
import com.andlife.nachoserver.entity.Report
import com.andlife.nachoserver.error.BusinessException
import com.andlife.nachoserver.repository.guestbook.GuestBookRepository
import com.andlife.nachoserver.repository.invitation.InvitationRepository
import com.andlife.nachoserver.repository.report.ReportRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.request.report.CreateReportRequest
import com.andlife.nachoserver.response.CommonResponseCode
import com.andlife.nachoserver.response.report.CreateReportResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReportService(
    private val reportRepository: ReportRepository,
    private val invitationRepository: InvitationRepository,
    private val guestBookRepository: GuestBookRepository,
    private val userRepository: UserRepository
) {
    fun createReport(authContext: AuthContext, request: CreateReportRequest) {
        val reporterId = when (authContext) {
            is AuthContext.Member -> authContext.userId
            is AuthContext.Guest -> throw BusinessException(CommonResponseCode.FORBIDDEN)
        }

        validateTargetExists(request.targetType, request.targetId)

        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                reporterId, request.targetType, request.targetId
            )
        ) {
            throw BusinessException(CommonResponseCode.CONFLICT, "이미 신고한 대상입니다.")
        }

        val reporter = userRepository.getReferenceById(reporterId)

        val report = Report(
            reporter = reporter,
            targetType = request.targetType,
            targetId = request.targetId,
            reason = ReportReason.safeValueOf(request.reason),
            description = request.description
        )

        reportRepository.save(report)
    }

    private fun validateTargetExists(targetType: ReportTargetType, targetId: Long) {
        val exists = when (targetType) {
            ReportTargetType.INVITATION -> invitationRepository.existsById(targetId)
            ReportTargetType.GUESTBOOK -> guestBookRepository.existsById(targetId)
        }
        if (!exists) throw BusinessException(CommonResponseCode.NOT_FOUND, "신고 대상이 존재하지 않습니다.")
    }
}