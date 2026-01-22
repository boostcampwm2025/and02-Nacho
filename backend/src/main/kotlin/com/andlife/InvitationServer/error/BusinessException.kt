package com.andlife.InvitationServer.error

import com.andlife.InvitationServer.response.CommonResponseCode

class BusinessException(
    val responseCode: CommonResponseCode,
    override val message: String? = responseCode.message
) : RuntimeException(message)