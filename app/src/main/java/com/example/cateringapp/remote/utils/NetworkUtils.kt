package com.example.cateringapp.remote.utils

import android.util.Log
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.HttpURLConnection as HttpCodes

sealed class NetworkResult<out T> {
    data object Unauthorized : NetworkResult<Nothing>()
    data object ConnError : NetworkResult<Nothing>()
    data object SockTimeout : NetworkResult<Nothing>()
    data object SuccessNullBody : NetworkResult<Nothing>()
    class Success<T>(val data: T) : NetworkResult<T>()
    class HttpError(val code: Int, msg: String) : NetworkResult<Nothing>()
    class Exception(val e: Throwable) : NetworkResult<Nothing>()
}

fun <T> resultHandler(request: () -> Result<T>): NetworkResult<T> {
    val result = request()
    if (result.isSuccess) {
        val data = result.getOrElse { return NetworkResult.SuccessNullBody }
        return NetworkResult.Success(data)
    } else {
        Log.d("REMOTE_ERROR",
            "Request unsuccessful. Throwable:${result.exceptionOrNull()?.message} \n ${result.exceptionOrNull()?.printStackTrace()}")
        val throwable = result.exceptionOrNull()
            ?: UnknownError("Remote request error unknown")
        if (throwable is HttpException){
            when(throwable.code())  {
                HttpCodes.HTTP_UNAUTHORIZED -> return NetworkResult.Unauthorized
                else -> return NetworkResult.HttpError(throwable.code(),throwable.message())
            }
        }else if (throwable is ConnectException){
            return NetworkResult.ConnError
        }else if (throwable is SocketTimeoutException){
            return NetworkResult.SockTimeout
        }else{
            return NetworkResult.Exception(throwable)
        }
    }
}