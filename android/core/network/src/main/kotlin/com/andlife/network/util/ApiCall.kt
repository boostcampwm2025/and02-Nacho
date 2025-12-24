package com.andlife.network.util

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.BaseResponse
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

suspend fun <T> apiCall(call: suspend () -> BaseResponse<T>): Result<T, DataError> {

    return try {
        val response = call()
        when (response.code) {
            in 200..299 -> {
                if (response.data == null) Result.Error(DataError.Network.NOT_FOUND, response.message)
                else Result.Success(response.data)
            }
            401 -> Result.Error(DataError.Network.UNAUTHORIZED, response.message)
            else -> Result.Error(DataError.Network.UNKNOWN, response.message)
        }
    } catch (e: HttpException) {
        when (e.code()) {
            408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT, e.message)
            in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR, e.message)
            else -> Result.Error(DataError.Network.UNKNOWN, e.message)
        }
    } catch (e: SerializationException) {
        Result.Error(DataError.Network.SERIALIZATION, e.message)
    } catch (e: Exception) {
        Result.Error(DataError.Network.UNKNOWN, e.message)
    }
}
