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
class AuthUserIdResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthUserId::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val userIdHeader = webRequest.getHeader("X-AndLife-User-Id")

        // 파라미터가 null을 허용하는지 확인
        val isNullable = parameter.isOptional

        if (userIdHeader.isNullOrBlank()) {
            if (isNullable) return null
            throw BusinessException(CommonResponseCode.UNAUTHORIZED)
        }

        // 헤더가 있지만 숫자가 아닌 경우
        return userIdHeader.toLongOrNull()
            ?: if (isNullable) null else throw BusinessException(CommonResponseCode.BAD_REQUEST)
    }
}