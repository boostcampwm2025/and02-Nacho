package com.andlife.data.repository.thankscard.mapper

import com.andlife.data.repository.invitation.mapper.toDomain
import com.andlife.data.repository.invitation.mapper.toDto
import com.andlife.domain.model.card.NachoCard
import com.andlife.network.model.card.NachoCardDto
import com.andlife.network.model.thankscard.ThanksCardRequest
import com.andlife.network.model.thankscard.ThanksCardResponse
import kotlinx.serialization.json.Json

fun NachoCard.toThanksCardRequest(json: Json): ThanksCardRequest{
    return ThanksCardRequest(
        contentJson = json.encodeToString(this.toDto()),
        backgroundColor = backgroundColor,
        backgroundImageUrl = backgroundImageUrl,
    )
}

fun ThanksCardResponse.toDomain(json: Json): NachoCard {
    val nachoCardDto = json.decodeFromString<NachoCardDto>(contentJson)
   return nachoCardDto.toDomain(id = id)
}
