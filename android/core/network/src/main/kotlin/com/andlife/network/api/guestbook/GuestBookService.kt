package com.andlife.network.api.guestbook

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GuestBookService {
    @GET("/api/invitations/{invitationId}/collection")
    suspend fun getMediaCollection(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<List<CollectionResponse>>
}
