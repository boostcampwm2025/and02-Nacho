package com.andlife.network.api.guestbook

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GuestBookService {
    @GET("/api/invitations/{invitationId}/collection")
    suspend fun getMediaCollection(
        @Path("invitationId") invitationId: Long,
    ): BaseResponse<List<CollectionResponse>>

    @GET("/api/invitations/{invitationId}/guestbooks")
    suspend fun getGuestBooksByInvitationId(
        @Path("invitationId") invitationId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<PagingResponse<GuestBookResponse>>

    @POST("/api/invitations/{invitationId}/guestbooks")
    suspend fun createGuestBook(
        @Path("invitationId") invitationId: Long,
        @Body request: GuestBookRequest,
    ): BaseResponse<GuestBookResponse>

    @PUT("/api/guestbooks/{guestBookId}")
    suspend fun updateGuestBook(
        @Path("guestBookId") guestBookId: Long,
        @Body request: UpdateGuestBookRequest,
    ): BaseResponse<GuestBookResponse>
}
