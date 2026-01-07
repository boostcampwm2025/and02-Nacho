package com.andlife.network.model

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class BaseResponse<T>(
    val code: Int,
    val data: T?,
    val message: String?,
)
