package com.andlife.nachoserver.request.thankscard

data class ThanksCardRequest(
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)