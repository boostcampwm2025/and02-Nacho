package com.andlife.nachoserver.constant

enum class ReportTargetType {
    INVITATION, GUESTBOOK
}

enum class ReportReason {
    SPAM,
    VIOLENT_HATE,
    SEXUAL_CONTENT,
    FRAUD_FALSE_INFO,
    HARASSMENT_PROFANITY,
    COPYRIGHT_INFRINGEMENT,
    ETC;

    companion object {
        fun safeValueOf(type: String, default: ReportReason = ETC): ReportReason {
            return try {
                valueOf(type)
            } catch (e: IllegalArgumentException) {
                default
            }
        }
    }
}

enum class ReportStatus {
    PENDING,
    REVIEWED,
    DISMISSED
}