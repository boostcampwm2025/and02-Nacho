package com.andlife.network.api.guestbook

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import com.andlife.network.model.invitation.guestbook.CreateGuestBookRequest
import com.andlife.network.model.invitation.guestbook.GuestBookResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GuestBookService {
    @POST("/api/invitations/guestbook")
    suspend fun createGuestBook(
        @Body request: CreateGuestBookRequest,
    ): BaseResponse<GuestBookResponse>

    @GET("/api/invitations/{invitationId}/collection")
    suspend fun getMediaCollection(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<List<CollectionResponse>>
}
