package com.andlife.nachoserver.constant

enum class ReportTargetType {
    INVITATION, GUESTBOOK
}

enum class ReportReason {
    SPAM,                   // 스팸/광고
    INAPPROPRIATE_CONTENT,  // 불쾌한 콘텐츠
    HARASSMENT,             // 괴롭힘/욕설
    FAKE_INFORMATION,       // 허위 정보
    COPYRIGHT,              // 저작권 침해
    OTHER
}

enum class ReportStatus {
    PENDING,    // 검토 대기
    REVIEWED,   // 검토 완료
    DISMISSED   // 반려
}