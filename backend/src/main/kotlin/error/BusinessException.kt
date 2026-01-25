package error

import com.andlife.nachoserver.response.ResponseCode

class BusinessException(
    val responseCode: ResponseCode,
    override val message: String = responseCode.message
) : RuntimeException(message)