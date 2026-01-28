@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.model.invitation

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class JoinResponse(
    val invitationId: Long,
    val isMember: Boolean,
    val alreadyJoined: Boolean
)
