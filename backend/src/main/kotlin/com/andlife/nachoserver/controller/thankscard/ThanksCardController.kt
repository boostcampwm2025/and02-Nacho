package com.andlife.nachoserver.controller.thankscard

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.request.thankscard.ThanksCardRequest
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.response.CommonResponseCode
import com.andlife.nachoserver.response.thankscard.ThanksCardResponse
import com.andlife.nachoserver.service.thankscard.ThanksCardService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/invitations")
class ThanksCardController(
    private val thanksCardService: ThanksCardService
) {

    @PostMapping("/{invitationId}/thanks-card")
    fun createThanksCard(
        @PathVariable invitationId: Long,
        @RequestBody request: ThanksCardRequest,
        authContext: AuthContext
    ): BaseResponse<Long> {
        if (authContext !is AuthContext.Member) {
            return BaseResponse.error(CommonResponseCode.UNAUTHORIZED)
        }

        return try {
            val response = thanksCardService.createThanksCard(invitationId, request)
            BaseResponse.success(response.id)
        } catch (e: NoSuchElementException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: IllegalStateException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.BAD_REQUEST,
                customMessage = e.message
            )
        } catch (e: Exception) {
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{invitationId}/thanks-card")
    fun getThanksCard(
        @PathVariable invitationId: Long
    ): BaseResponse<ThanksCardResponse> {
        return try {
            val response = thanksCardService.getThanksCard(invitationId)
            BaseResponse.success(response)
        } catch (e: NoSuchElementException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: Exception) {
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/thanks-cards/{cardId}")
    fun updateThanksCard(
        @PathVariable cardId: Long,
        @RequestBody request: ThanksCardRequest,
        authContext: AuthContext
    ): BaseResponse<ThanksCardResponse> {
        if (authContext !is AuthContext.Member) {
            return BaseResponse.error(CommonResponseCode.UNAUTHORIZED)
        }

        return try {
            val response = thanksCardService.updateThanksCard(cardId, request)
            BaseResponse.success(response)
        } catch (e: NoSuchElementException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: Exception) {
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{invitationId}/thanks-card")
    fun deleteThanksCard(
        @PathVariable invitationId: Long,
        authContext: AuthContext
    ): BaseResponse<Unit> {
        if (authContext !is AuthContext.Member) {
            return BaseResponse.error(CommonResponseCode.UNAUTHORIZED)
        }

        return try {
            thanksCardService.deleteThanksCard(invitationId)
            BaseResponse.success(Unit)
        } catch (e: Exception) {
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }
}
