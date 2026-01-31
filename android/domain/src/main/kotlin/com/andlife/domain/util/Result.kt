package com.andlife.domain.util

import com.andlife.domain.error.DataError
import com.andlife.domain.error.InvitationError
import java.io.IOException

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

inline fun <D, E : InvitationError> Result<D, E>.onFailure(action: (error: E, msg: String?) -> Unit
): Result<D, E> {
    if (this is Result.Error) {
        action(error, message)
    }
    return this
}

fun <D, E : InvitationError> Result<D, E>.getOrNull(): D? {
    return when {
        this is Result.Success -> data
        else -> null
    }
}

inline fun <R> runResultCatching(block: () -> R): Result<R, InvitationError> {
    return try {
        Result.Success(block())
    } catch (e: IOException) {
        Result.Error(DataError.Local.IOEXCEPTION, e.message)
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN, e.message)
    }
}
