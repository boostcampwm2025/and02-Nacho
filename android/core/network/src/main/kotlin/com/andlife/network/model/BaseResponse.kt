package com.andlife.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val code: Int,
    val data: T?,
    val message: String?,
)
