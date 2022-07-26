package com.rakuseru.storyapp1.utils

sealed class Result<T>(val data: T? = null, val message: String? = null)  {
    // Loading Status
    class Loading<T> : Result<T>()

    // Success Status
    class Success<T>(data: T) : Result<T>(data)

    // Error Status
    class Error<T>(message: String?, data: T? = null) : Result<T>(data, message)
}