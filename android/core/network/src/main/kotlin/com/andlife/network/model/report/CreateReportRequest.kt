@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.model.report

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class CreateReportRequest(
    val targetType: String,
    val targetId: Long,
    val reason: String,
    val description: String?,
)
