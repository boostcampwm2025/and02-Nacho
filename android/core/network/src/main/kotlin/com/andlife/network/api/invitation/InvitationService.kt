package com.andlife.network.api.invitation

import com.andlife.network.model.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface InvitationService {

    @GET("/api/invitations/{invitationId}")
    suspend fun getInvitation(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<InvitationResponse>
}
