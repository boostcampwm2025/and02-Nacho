package com.andlife.nachoserver.controller.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.auth.dto.UserResponse
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.service.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getMe(authContext: AuthContext): BaseResponse<UserResponse> {
        val user = userService.getMe(authContext)
        return BaseResponse.success(user)
    }
}
