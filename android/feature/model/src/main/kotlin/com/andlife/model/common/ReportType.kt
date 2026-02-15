package com.andlife.model.common

import com.andlife.model.R

enum class ReportTargetType {
    INVITATION, GUESTBOOK
}

enum class ReportReason(val stringResId: Int) {
    SPAM(R.string.txt_spam),              // 스팸
    VIOLENT_HATE(R.string.txt_violent_hate), // 폭력적이거나 혐오스러운 콘텐츠
    SEXUAL_CONTENT(R.string.txt_sexual_content), // 성적 콘텐츠
    FRAUD_FALSE_INFO(R.string.txt_fraud_false_info), // 사기 또는 허위 정보
    HARASSMENT_PROFANITY(R.string.txt_harassment_profanity), // 괴롭힘 또는 욕설
    COPYRIGHT_INFRINGEMENT(R.string.txt_copyright_infringement), // 저작권 침해
    ETC(R.string.txt_etc); // 기타

    companion object {
        fun safeValueOf(type: String, default: ReportReason = ETC): ReportReason = try {
            ReportReason.valueOf(type)
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}
