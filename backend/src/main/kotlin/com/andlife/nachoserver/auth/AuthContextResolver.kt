package com.andlife.nachoserver.auth

import com.andlife.nachoserver.auth.exception.TokenExpiredException
import com.andlife.nachoserver.auth.jwt.JwtProvider
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthContextResolver(
    private val jwtProvider: JwtProvider
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthContext::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): AuthContext {
        val authHeader = webRequest.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ")

            if (jwtProvider.validateToken(token)) {
                val userId = jwtProvider.getUserId(token)
                return AuthContext.Member(userId)
            }

            if (jwtProvider.isTokenExpired(token)) {
                throw TokenExpiredException()
            }

        }

        val invitationIdsHeader = webRequest.getHeader("Nacho-Guest-Invitation-Ids")
        val invitationIds = invitationIdsHeader?.split(",")
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?: emptyList()

        return AuthContext.Guest(invitationIds)
    }
}