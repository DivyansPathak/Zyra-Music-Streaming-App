package com.zyra.music.zyra.data.remote

import android.util.Log
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.network.sockets.SocketTimeoutException
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import java.net.UnknownHostException

const val TAG = "SafeCall"
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError> {

    val response = try {
        execute()
    } catch (e: UnknownHostException) {
        return Result.Failure(DataError.NoInternet)
    } catch (e: UnresolvedAddressException) {
        return Result.Failure(DataError.NoInternet)
    } catch (e: SocketTimeoutException) {
        return Result.Failure(DataError.RequestTimeOut)
    } catch (e: Exception) {
        return Result.Failure(DataError.UnknownError(e.message))
    }

    return when (response.status.value) {

        in 200..299 -> {
            Log.i(TAG, "Network call successful with status: ${response.status.value}")
            try {
                val trackDto = response.body<T>()
                Log.i(TAG, "JSON parsing successful. Parsed data: $trackDto")
                Result.Success(trackDto)
            } catch (e: JsonConvertException) {
                Log.e(TAG, "JSON parsing FAILED.", e) // Log the actual exception
                Result.Failure(DataError.SerializationError)
            } catch (e: NoTransformationFoundException) {
                Log.e(TAG, "JSON parsing FAILED.", e) // Log the actual exception
                Result.Failure(DataError.SerializationError)
            }
        }

        408 -> Result.Failure(DataError.RequestTimeOut)
        429 -> Result.Failure(DataError.TooManyRequest)
        in 500..599 -> Result.Failure(DataError.ServerError)
        else -> {
            Log.w(TAG, "Network call failed with status: ${response.status.value}")
            Result.Failure(DataError.UnknownError())
        }
    }
}