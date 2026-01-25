package error

import com.andlife.InvitationServer.response.ResponseCode

class BusinessException(
    val responseCode: ResponseCode,
    override val message: String = responseCode.message
) : RuntimeException(message)