package com.andlife.domain.error

sealed interface DataError : InvitationError {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        SERVER_ERROR,
        UNAUTHORIZED,
        NOT_FOUND,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
    }

    enum class LocalImage : DataError {
        DecodeFailed,
        NotFound,
        OutOfMemory,
        CompressFailed,
    }
}

