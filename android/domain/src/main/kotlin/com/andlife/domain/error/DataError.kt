package com.andlife.domain.error

sealed interface DataError : InvitationError {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        SERVER_ERROR,
        UNAUTHORIZED,
        NOT_FOUND,
        CONFLICT,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
        IOEXCEPTION,
        UNKNOWN,
    }

    enum class LocalImage : DataError {
        DecodeFailed,
        NotFound,
        OutOfMemory,
        CompressFailed,
    }

    enum class Validation : DataError {
        FILE_TOO_LARGE,
    }
}

