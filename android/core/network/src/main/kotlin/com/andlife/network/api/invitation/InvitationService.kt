package com.andlife.network.api.invitation

import com.andlife.network.model.BaseResponse
import retrofit2.http.Body
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InvitationService {

    @GET("/api/invitations/{invitationId}")
    suspend fun getInvitation(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<InvitationResponse>

    @GET("/api/invitations/joined")
    suspend fun getParticipantInvitations(
        @Query("status") status: String,
        @Query("sortType") sortType: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): BaseResponse<PagingResponse<InvitationSummaryResponse>>

    @GET("/api/invitations/mine")
    suspend fun getMyInvitations(
        @Query("status") status: String,
        @Query("sortType") sortType: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): BaseResponse<PagingResponse<InvitationSummaryResponse>>

    @POST("/api/invitations")
    suspend fun createInvitation(
        @Body request: CreateInvitationRequest,
    ): BaseResponse<InvitationResponse>


}
