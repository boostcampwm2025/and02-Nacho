package com.andlife.nachoserver.response

import org.springframework.http.ResponseEntity

data class BaseResponse<out T>(
    val code: Int,
    val data: T?,
    val message: String?
) {
    companion object {

        fun <T> success(
            data: T? = null,
            responseCode: ResponseCode = CommonResponseCode.OK
        ): BaseResponse<T> {
            return BaseResponse(
                code = responseCode.httpStatus.value(),
                data = data,
                message = responseCode.message
            )
        }

        fun error(
            responseCode: ResponseCode,
            customMessage: String? = null
        ): BaseResponse<Nothing> {
            return BaseResponse(
                code = responseCode.httpStatus.value(),
                data = null,
                message = customMessage ?: responseCode.message
            )
        }

        fun errorWithStatus(
            responseCode: ResponseCode,
            customMessage: String? = null
        ): ResponseEntity<BaseResponse<Nothing>> {
            return ResponseEntity
                .status(responseCode.httpStatus)
                .body(
                    BaseResponse(
                        code = responseCode.httpStatus.value(),
                        data = null,
                        message = customMessage ?: responseCode.message
                    )
                )
        }
    }
}