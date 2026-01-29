package com.andlife.nachoserver.auth.exception


class TokenExpiredException(
    message: String = "Access token has expired"
) : RuntimeException(message)
