package com.andlife.nachoserver.response.invitation

data class JoinResponse(
    val invitationId: Long,
    val isMember: Boolean,
    val alreadyJoined: Boolean
)