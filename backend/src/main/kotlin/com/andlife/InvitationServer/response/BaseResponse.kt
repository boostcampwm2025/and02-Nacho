package com.andlife.InvitationServer.response

data class BaseResponse<T>(
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
    }

}