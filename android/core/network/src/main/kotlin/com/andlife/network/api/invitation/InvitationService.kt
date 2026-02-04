package com.andlife.network.api.invitation

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.InvitationCardRequest
import com.andlife.network.model.invitation.InvitationResponse
import com.andlife.network.model.invitation.InvitationSaveRequest
import com.andlife.network.model.invitation.InvitationSummaryResponse
import com.andlife.network.model.invitation.JoinResponse
import com.andlife.network.model.invitation.UpcomingInvitationResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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
        @Query("size") size: Int
    ): BaseResponse<PagingResponse<InvitationSummaryResponse>>

    @GET("/api/invitations/mine")
    suspend fun getMyInvitations(
        @Query("status") status: String,
        @Query("sortType") sortType: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<PagingResponse<InvitationSummaryResponse>>

    @GET("/api/invitations/upcoming")
    suspend fun getUpcomingInvitations(
        @Query("days") days: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<PagingResponse<UpcomingInvitationResponse>>

    @POST("/api/invitations")
    suspend fun createInvitation(
        @Body request: InvitationSaveRequest,
    ): BaseResponse<InvitationResponse>

    @PUT("/api/invitations/{invitationId}")
    suspend fun updateInvitation(
        @Path("invitationId") invitationId: Long,
        @Body request: InvitationSaveRequest,
    ): BaseResponse<InvitationResponse>

    @DELETE("/api/invitations/{invitationId}")
    suspend fun deleteInvitation(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<Unit>

    @POST("/api/invitations/{invitationId}/join")
    suspend fun joinInvitation(
        @Path("invitationId") invitationId: Long
    ): BaseResponse<JoinResponse>

    @POST("/api/invitations/{invitationId}/leave")
    suspend fun leaveInvitation(
        @Path("invitationId") invitationId: Long
    ): BaseResponse<Unit>

    @POST("/api/invitations/sync")
    suspend fun syncInvitations(
        @Body invitationIds: List<Long>
    ): BaseResponse<Unit>

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
