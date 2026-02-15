package com.andlife.model.common

import com.andlife.model.R

enum class ReportTargetType {
    INVITATION, GUESTBOOK
}

enum class ReportReason(val stringResId: Int) {
    SPAM(R.string.txt_spam),
    VIOLENT_HATE(R.string.txt_violent_hate),
    SEXUAL_CONTENT(R.string.txt_sexual_content),
    FRAUD_FALSE_INFO(R.string.txt_fraud_false_info),
    HARASSMENT_PROFANITY(R.string.txt_harassment_profanity),
    COPYRIGHT_INFRINGEMENT(R.string.txt_copyright_infringement),
    ETC(R.string.txt_etc);

    companion object {
        fun safeValueOf(type: String, default: ReportReason = ETC): ReportReason = try {
            ReportReason.valueOf(type)
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}
