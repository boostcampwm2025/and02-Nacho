package com.andlife.network.api.invitation

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.PagingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InvitationService {

    @GET("/api/invitations/{invitationId}")
    suspend fun getInvitation(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<InvitationResponse>

    @POST("/api/invitations")
    suspend fun createInvitation(
        @Body request: CreateInvitationRequest,
    ): BaseResponse<InvitationResponse>

    @GET("/api/invitations/upcoming")
    suspend fun getUpcomingInvitations(
        @Query("days") days: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<PagingResponse<UpcomingInvitationResponse>>
}
