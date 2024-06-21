package com.example.cateringapp.remote.utils

/**
 * Default network result handler. All errors except "Unauthorized" sent to one "onOtherState" lambda.
 */
fun <T> checkNetworkResult(
    res: NetworkResult<T>,
    onSuccess: (res: NetworkResult.Success<T>) -> Unit,
    onUnauthorized: () -> Unit,
    onOtherState: (res: NetworkResult<T>) -> Unit,
) {
    when (res) {
        is NetworkResult.Success -> onSuccess(res)
        NetworkResult.Unauthorized -> onUnauthorized()
        NetworkResult.SockTimeout -> onOtherState(res)
        NetworkResult.ConnError -> onOtherState(res)
        else -> onOtherState(res)
    }
}

sealed class RequestResult<out T> {
    data object None : RequestResult<Nothing>()
    data object Unauthorized : RequestResult<Nothing>()
    data object ServerUnreachable : RequestResult<Nothing>()
    data object ConnError : RequestResult<Nothing>()
    data object OtherError : RequestResult<Nothing>()
    data class Success<T>(val data: T) : RequestResult<T>()
}