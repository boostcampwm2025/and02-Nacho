package com.andlife.nachoserver.response

import org.springframework.http.HttpStatus

interface ResponseCode {
    val httpStatus: HttpStatus
    val message: String
    val name: String
}

enum class CommonResponseCode(
    override val httpStatus: HttpStatus,
    override val message: String
): ResponseCode {
    // 2xx 성공
    OK(HttpStatus.OK, "성공적으로 처리되었습니다."),

    // 4xx 클라이언트 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 정보를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

    // 5xx 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
}