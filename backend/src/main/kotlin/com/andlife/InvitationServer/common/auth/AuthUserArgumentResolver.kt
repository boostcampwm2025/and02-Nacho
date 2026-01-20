package com.andlife.InvitationServer.common.auth

import com.andlife.InvitationServer.response.CommonResponseCode
import error.BusinessException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthUserArgumentResolver : HandlerMethodArgumentResolver {

    companion object {
        private const val USER_ID_HEADER = "Nacho-User-Id"
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthUser::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long {
        val userIdHeader = webRequest.getHeader(USER_ID_HEADER)
            ?: throw BusinessException(CommonResponseCode.UNAUTHORIZED)

        return try {
            userIdHeader.toLong()
        } catch (e: NumberFormatException) {
            throw BusinessException(CommonResponseCode.UNAUTHORIZED)
        }
    }
}