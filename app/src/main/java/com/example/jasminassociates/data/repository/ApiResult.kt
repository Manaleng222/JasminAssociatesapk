// ApiResult.kt
package com.example.jasminassociates.data.repository

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Failure(val exception: Exception) : ApiResult<Nothing>()
}

val <T> ApiResult<T>.isSuccess: Boolean
    get() = this is ApiResult.Success

val <T> ApiResult<T>.isFailure: Boolean
    get() = this is ApiResult.Failure
fun <T> ApiResult<T>.getOrNull(): T? = when (this) {
    is ApiResult.Success -> data
    is ApiResult.Failure -> null
}