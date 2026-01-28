package com.andlife.data.repository.invitation.mapper

import com.andlife.domain.model.invitation.InvitationJoin
import com.andlife.network.model.invitation.JoinResponse

fun JoinResponse.toDomain(): InvitationJoin {
    return InvitationJoin(
        invitationId = this.invitationId,
        isMember = this.isMember,
        alreadyJoined = this.alreadyJoined
    )
}
