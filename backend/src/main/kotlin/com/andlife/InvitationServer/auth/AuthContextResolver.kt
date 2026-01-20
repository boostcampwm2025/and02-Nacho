package com.andlife.InvitationServer.auth

import com.andlife.InvitationServer.error.BusinessException
import com.andlife.InvitationServer.response.CommonResponseCode
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthContextResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthContext::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): AuthContext {
        val userIdHeader = webRequest.getHeader("Nacho-User-Id")

        if (!userIdHeader.isNullOrBlank()) {
            val userId = userIdHeader.toLongOrNull()
                ?: throw BusinessException(CommonResponseCode.BAD_REQUEST)
            return AuthContext.Member(userId)
        }

        val invitationIdsHeader = webRequest.getHeader("Nacho-Guest-Invitation-Ids")
        val invitationIds = invitationIdsHeader?.split(",")
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?: emptyList()

        return AuthContext.Guest(invitationIds)
    }
}