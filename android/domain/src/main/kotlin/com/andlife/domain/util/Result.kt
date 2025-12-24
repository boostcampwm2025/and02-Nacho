package com.andlife.domain.util

import com.andlife.domain.error.InvitationError

sealed interface Result<out D, out E: InvitationError> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: InvitationError>(val error: E): Result<Nothing, E>
}

inline fun <D, E : InvitationError> Result<D, E>.onSuccess(
    action: (data: D) -> Unit
): Result<D, E> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <D, E : InvitationError> Result<D, E>.onFailure(
    action: (error: E) -> Unit
): Result<D, E> {
    if (this is Result.Error) {
        action(error)
    }
    return this
}
