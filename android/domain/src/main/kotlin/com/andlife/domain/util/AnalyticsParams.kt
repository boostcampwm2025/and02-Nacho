package com.andlife.domain.util

enum class Screen(val value: String) {
    LOGIN("login"),
    HOME("home"),
    INVITATION("invitation"),
    MY_INVITATION("my_invitation"),
    INVITATION_DETAIL("invitation_detail"),
    INVITATION_DETAIL_GUEST_BOOK("invitation_detail_guest_book"),
    INVITATION_DETAIL_COLLECTION("invitation_detail_collection"),
    MY_INVITATION_DETAIL("my_invitation_detail"),
    MY_INVITATION_DETAIL_COLLECTION("my_invitation_detail_collection"),
    MY_INVITATION_DETAIL_GUEST_BOOK("my_invitation_detail_guest_book"),
    INVITATION_CREATE("invitation_create"),
    INVITATION_PREVIEW("invitation_preview"),
    SETTING("setting"),
}

enum class Button(val value: String) {
    KAKAO_LOGIN("kakao_login"),
    GUEST_LOGIN("guest_login"),
    INVITATION_SORT_UPCOMING("invitation_sort_upcoming"),
    INVITATION_SORT_PAST("invitation_sort_past"),
    MY_INVITATION_SORT_UPCOMING("my_invitation_sort_upcoming"),
    MY_INVITATION_SORT_PAST("my_invitation_sort_past"),
    DELETE_INVITATION("delete_invitation"),
    UPDATE_INVITATION("update_invitation"),
    INVITATION_REPORT("invitation_report"),
    INVITATION_LEAVE("invitation_leave"),
    INVITATION_CREATE("invitation_create"),
    PREVIEW_INVITATION("preview_invitation"),
    CREATE_INVITATION_CARD("create_invitation_card"),
    UPDATE_INVITATION_CARD("update_invitation_card"),
    CREATE_THANKS_CARD("create_thanks_card"),
    UPDATE_THANKS_CARD("update_thanks_card"),
    DELETE_THANKS_CARD("delete_thanks_card"),
    SHOW_THANKS_CARD("show_thanks_card"),
    UPLOAD_GUEST_BOOK("upload_guest_book"),
    UPDATE_GUEST_BOOK("update_guest_book"),
    DELETE_GUEST_BOOK("delete_guest_book"),
    GUEST_BOOK_REPORT("guest_book_report"),
    SETTING_LOGOUT("setting_logout"),
    SETTING_SIGN_OUT("setting_sign_out"),
    SETTING_NICKNAME("setting_nickname"),
    SETTING_PROFILE_IMAGE("setting_profile"),
    MEDIA_DOWNLOAD("media_download"),
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
    SUCCESS_REPORT_INVITATION("success_report_invitation"),
    FAIL_REPORT_INVITATION("fail_report_invitation"),
    SUCCESS_REPORT_GUESTBOOK("success_report_guestbook"),
    FAIL_REPORT_GUESTBOOK("fail_report_guestbook"),
    GUEST_BOOK_MAX_MEDIA("guest_book_max_media"),
    GUEST_BOOK_MAX_SIZE("guest_book_max_size"),
}
