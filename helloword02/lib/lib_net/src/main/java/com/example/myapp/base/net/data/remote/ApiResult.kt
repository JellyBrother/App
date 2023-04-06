package com.example.myapp.base.net.data.remote

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T) : ApiResult<T>()

    data class Failure(val throwable: Throwable?) : ApiResult<Nothing>()
}

inline fun <reified T> ApiResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is ApiResult.Success) {
        success(value)
    }
}

inline fun <reified T> ApiResult<T>.doFailure(failure: (Throwable?) -> Unit) {
    if (this is ApiResult.Failure) {
        failure(throwable)
    }
}
