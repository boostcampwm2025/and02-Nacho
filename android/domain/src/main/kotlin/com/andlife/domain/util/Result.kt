package com.andlife.domain.util

import com.andlife.domain.error.InvitationError

sealed interface Result<out D, out E : InvitationError> {
    data class Success<out D>(
        val data: D,
    ) : Result<D, Nothing>

    data class Error<out E : InvitationError>(
        val error: E,
        val message: String? = null,
    ) : Result<Nothing, E>
}

inline fun <T, E : InvitationError, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Error -> Result.Error(error, message)
        is Result.Success -> Result.Success(transform(data))
    }

inline fun <D, E : InvitationError> Result<D, E>.onSuccess(action: (data: D) -> Unit
): Result<D, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <D, E : InvitationError> Result<D, E>.onFailure(action: (error: E) -> Unit
): Result<D, E> {
    if (this is Result.Error) {
        action(error)
    }
    return this
}

fun <D, E : InvitationError> Result<D, E>.getOrNull(): D? {
    return when {
        this is Result.Success -> data
        else -> null
    }
}
