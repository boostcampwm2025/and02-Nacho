package com.andlife.nachoserver.response.thankscard

import com.andlife.nachoserver.entity.ThanksCard

data class ThanksCardResponse(
    val id: Long,
    val invitationId: Long,
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?
)

fun ThanksCard.toResponse() = ThanksCardResponse(
    id = this.id,
    invitationId = this.invitation.id,
    contentJson = this.contentJson,
    backgroundColor = this.backgroundColor,
    backgroundImageUrl = this.backgroundImageUrl
)
