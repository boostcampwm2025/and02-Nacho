package com.andlife.network.model

data class BaseResponse<T>(
    val code: Int,
    val data: T?,
    val message: String?,
)
