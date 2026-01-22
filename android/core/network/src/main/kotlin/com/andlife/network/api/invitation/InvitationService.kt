package com.andlife.network.api.invitation

import com.andlife.network.model.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InvitationService {

    @GET("/api/invitations/{invitationId}")
    suspend fun getInvitation(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<InvitationResponse>

    @POST("/api/invitations")
    suspend fun createInvitation(
        @Body request: CreateInvitationRequest,
    ): BaseResponse<InvitationResponse>

    @POST("api/invitations/{invitationId}/cards")
    suspend fun createInvitationCard(
        @Path("invitationId") invitationId: Long,
        @Body request: InvitationCardRequest,
    ): BaseResponse<Long>

    @PUT("api/invitations/cards/{cardId}")
    suspend fun updateInvitationCard(
        @Path("cardId") cardId: Long,
        @Body request: InvitationCardRequest,
    ): BaseResponse<Long>
}
