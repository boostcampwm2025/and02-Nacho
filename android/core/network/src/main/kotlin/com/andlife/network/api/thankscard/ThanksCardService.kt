package com.andlife.network.api.thankscard

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.thankscard.ThanksCardRequest
import com.andlife.network.model.thankscard.ThanksCardResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ThanksCardService {

    @POST("api/invitations/{invitationId}/thanks-card")
    suspend fun createThanksCard(
        @Path("invitationId") invitationId: Long,
        @Body request: ThanksCardRequest
    ): BaseResponse<Long>

    @GET("api/invitations/thanks-card/{cardId}")
    suspend fun getThanksCard(
        @Path("cardId") cardId: Long
    ): BaseResponse<ThanksCardResponse>

    @PUT("api/invitations/thanks-card/{cardId}")
    suspend fun updateThanksCard(
        @Path("cardId") cardId: Long,
        @Body request: ThanksCardRequest
    ): BaseResponse<ThanksCardResponse>

    @DELETE("api/invitations/{invitationId}/thanks-card")
    suspend fun deleteThanksCard(
        @Path("invitationId") invitationId: Long
    ): BaseResponse<Long>
}
