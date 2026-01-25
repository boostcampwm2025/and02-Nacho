package com.andlife.nachoserver.error

import com.andlife.nachoserver.response.CommonResponseCode

class BusinessException(
    val responseCode: CommonResponseCode,
    override val message: String? = responseCode.message
) : RuntimeException(message)