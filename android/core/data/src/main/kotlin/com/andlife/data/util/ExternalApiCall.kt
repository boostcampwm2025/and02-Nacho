package com.andlife.data.util

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

suspend fun <T> externalApiCall(call: suspend () -> T): Result<T, DataError> =
    try {
        val response = call()
        Result.Success(response)
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> Result.Error(DataError.Network.UNAUTHORIZED, e.message)
            404 -> Result.Error(DataError.Network.NOT_FOUND, e.message)
            408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT, e.message)
            in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR, e.message)
            else -> Result.Error(DataError.Network.UNKNOWN, e.message)
        }
    } catch (e: SerializationException) {
        Result.Error(DataError.Network.SERIALIZATION, e.message)
    } catch (e: Exception) {
        Result.Error(DataError.Network.UNKNOWN, e.message)
    }
