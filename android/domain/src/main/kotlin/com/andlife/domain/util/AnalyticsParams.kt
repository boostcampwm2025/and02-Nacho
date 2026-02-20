package com.andlife.domain.util

enum class Screen(val value: String) {
    HOME("home"),
    LOGIN("login"),
    EDITOR("editor"),
    INVITATION("invitation"),
    INVITATION_DETAIL("invitation_detail"),
    MY_INVITATION("my_invitation"),
    MY_INVITATION_DETAIL("my_invitation_detail"),
    SETTING("setting"),
    INVITE_CARD("invite_card"),
    THANKS_CARD("thanks_card"),
}

enum class Button(val value: String) {
    // 로그인
    KAKAO_LOGIN("kakao_login"),
    GUEST_LOGIN("guest_login"),
    // 정렬
    SORT_UPCOMING("sort_upcoming"),
    SORT_PAST("sort_past"),
    // 초대장 CRUD
    CREATE_MY_INVITATION("create_invitation"),
    EDIT_MY_INVITATION("edit_invitation"),
    CREATE_INVITE_CARD("create_invitation_card"),
    // 초대카드 CRUD
    EDIT_INVITE_CARD("edit_invitation_card"),
    DELETE_MY_INVITATION("delete_invitation"),
    CREATE_THANKS_CARD("create_thanks_card"),
    UPDATE_THANKS_CARD("update_thanks_card"),
    // 초대장 떠나기
    LEAVE_INVITATION("leave_invitation"),
    // 방명록
    TRY_UPLOAD_GUESTBOOK("try_upload_guestbook"),
    // 미디어 다운로드
    MEDIA_DOWNLOAD_("media_download"),
    // 프로필
    EDIT_PROFILE("edit_profile"),
    // 신고
    REPORT("report"),
}

enum class LoginMethod(val value: String) {
    KAKAO("kakao"),
    GUEST("guest"),
}

enum class ShareMethod(val value: String) {
    KAKAO_LINK("kakao_link"),
    CLIPBOARD("clipboard"),
}

enum class EventType(val value: String) {
    INVITATION_CREATED("invitation_created"),
    INVITATION_UPDATED("invitation_updated"),
    INVITATION_DELETED("invitation_deleted"),
    INVITATION_CARD_CREATED("invitation_card_created"),
    INVITATION_CARD_UPDATED("invitation_card_updated"),
    INVITATION_CARD_DELETED("invitation_card_deleted"),
    LOGIN_WITH_KAKAO("login_with_kakao"),
    LOGIN_WITH_GUEST("login_with_guest"),
    SUCCESS_UPLOAD_GUESTBOOK("success_upload_guestbook"),
    MEDIA_DOWNLOAD("media_download"),
    LEAVE_INVITATION("leave_invitation"),
    FAIL_LEAVE_INVITATION("fail_leave_invitation"),
    // 신고

}
