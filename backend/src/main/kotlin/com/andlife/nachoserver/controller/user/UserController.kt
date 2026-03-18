package com.andlife.nachoserver.controller.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.auth.dto.UserResponse
import com.andlife.nachoserver.request.user.RegisterFcmTokenRequest
import com.andlife.nachoserver.request.user.UpdateProfileRequest
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.response.user.RegisterFcmTokenResponse
import com.andlife.nachoserver.service.user.FcmTokenService
import com.andlife.nachoserver.service.user.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val fcmTokenService: FcmTokenService
) {

    @GetMapping("/me")
    fun getMe(authContext: AuthContext): BaseResponse<UserResponse> {
        val user = userService.getMe(authContext)
        return BaseResponse.success(user)
    }

    @DeleteMapping
    fun deleteUser(authContext: AuthContext): BaseResponse<UserResponse> {
        val user = userService.deleteUser(authContext)
        return BaseResponse.success(user)
    }

    @PatchMapping("/me")
    fun updateProfile(
        authContext: AuthContext,
        @RequestBody request: UpdateProfileRequest
    ): BaseResponse<UserResponse> {
        val user = userService.updateProfile(
            authContext = authContext,
            nickname = request.nickname,
            newProfileImageUrl = request.profileImageUrl
        )
        return BaseResponse.success(user)
    }

    @PutMapping("/fcm-token")
    fun putFcmToken(
        authContext: AuthContext,
        @RequestBody request: RegisterFcmTokenRequest
    ): BaseResponse<RegisterFcmTokenResponse> {
        val response = fcmTokenService.putFcmToken(authContext, request.fcmToken)
        return BaseResponse.success(response)
    }
}
