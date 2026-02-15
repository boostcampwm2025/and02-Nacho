package com.andlife.nachoserver.constant

enum class ReportTargetType {
    INVITATION, GUESTBOOK
}

enum class ReportReason {
    SPAM,                   // 스팸
    VIOLENT_HATE,          // 폭력적, 혐오스러운 콘텐츠
    SEXUAL_CONTENT,        // 성적인 콘텐츠
    FRAUD_FALSE_INFO,      // 사기, 거짓 정보 유포
    HARASSMENT_PROFANITY,  // 괴롭힘, 욕설
    COPYRIGHT_INFRINGEMENT, // 저작권 침해
    ETC                     // 기타
}

enum class ReportStatus {
    PENDING,    // 검토 대기
    REVIEWED,   // 검토 완료
    DISMISSED   // 반려
}