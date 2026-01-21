package com.andlife.InvitationServer.util

import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.response.CommonResponseCode
import jakarta.persistence.EntityNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(e: EntityNotFoundException): BaseResponse<Nothing> {
        return BaseResponse.error(
            responseCode = CommonResponseCode.NOT_FOUND,
            customMessage = e.message ?: "리소스를 찾을 수 없습니다."
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(e: Exception): BaseResponse<Nothing> {
        return BaseResponse.error(
            responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR,
            customMessage = "서버 내부 오류가 발생했습니다."
        )
    }
}