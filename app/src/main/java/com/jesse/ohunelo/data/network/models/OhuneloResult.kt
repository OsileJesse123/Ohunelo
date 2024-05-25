package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.util.UiText


sealed class OhuneloResult<T>(
    val data: T? = null,
    val error: Exception? = null
){

    class Success<T>(data: T): OhuneloResult<T>(data = data)
    class Error<T>(error: Exception, data: T? = null): OhuneloResult<T>(data, error)
}
