package com.andlife.data.util

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.BaseResponse
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

suspend fun <T> apiCall(call: suspend () -> BaseResponse<T>): Result<T, DataError> =
    try {
        val response = call()

        when (response.code) {
            in 200..299 -> {
                response.data?.let {
                    Result.Success(it) // it은 여기서 T (non-nullable)
                } ?: Result.Error(DataError.Network.NOT_FOUND, response.message)
            }

            401 -> {
                Result.Error(DataError.Network.UNAUTHORIZED, response.message)
            }

            else -> {
                Result.Error(DataError.Network.UNKNOWN, response.message)
            }
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
