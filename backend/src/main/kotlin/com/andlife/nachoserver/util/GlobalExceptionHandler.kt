package com.andlife.nachoserver.util

import com.andlife.nachoserver.auth.exception.TokenExpiredException
import com.andlife.nachoserver.error.BusinessException
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.response.CommonResponseCode
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpired(e: TokenExpiredException): ResponseEntity<BaseResponse<Nothing>> {
        return BaseResponse.errorWithStatus(CommonResponseCode.TOKEN_EXPIRED)
    }

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

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): BaseResponse<Nothing> {
        return BaseResponse.error(
            responseCode = e.responseCode,
            customMessage = e.message
        )
    }
}