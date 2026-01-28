package com.andlife.domain.model.invitation

data class InvitationJoin(
    val invitationId: Long,
    val isMember: Boolean,
    val alreadyJoined: Boolean
)
