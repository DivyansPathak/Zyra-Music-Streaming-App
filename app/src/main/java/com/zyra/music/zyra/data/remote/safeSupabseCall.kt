package com.zyra.music.zyra.data.remote

import android.util.Log
import com.zyra.music.zyra.domain.utils.DataError
import com.zyra.music.zyra.domain.utils.Result
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.UnknownHostException

suspend inline fun <T> safeSupabaseCall(
    execute :() -> T
) : Result<T, DataError>{

    return try {
        // The Supabase call is executed here. If it's successful,
        // we wrap the result in your Result.Success class.
        Result.Success(execute())

    }
    // This is the primary exception from Supabase for API errors (e.g., 4xx, 5xx).
    catch (e: RestException) {
        Log.e("SupabaseSafeCall", "API Error: ${e.message}", e)
        // You could add more logic here to check e.error, e.code, etc.
        return Result.Failure(DataError.ServerError)
    }
    // Ktor exceptions that the Supabase client might throw internally.
    catch (e: HttpRequestTimeoutException) {
        Log.e("SupabaseSafeCall", "Request Timeout", e)
        return Result.Failure(DataError.RequestTimeOut)
    }
    catch (e: UnknownHostException) {
        Log.e("SupabaseSafeCall", "No Internet / Unknown Host", e)
        return Result.Failure(DataError.NoInternet)
    }
    // Generic fallback for any other unexpected errors.
    catch (e: Exception) {
        Log.e("SupabaseSafeCall", "An unknown error occurred", e)
        return Result.Failure(DataError.UnknownError(e.message))
    }

}