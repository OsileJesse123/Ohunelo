package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.util.UiText


sealed class OhuneloResult<T>(
    val data: T? = null,
    val errorMessage: UiText? = null
){

    class Success<T>(data: T): OhuneloResult<T>(data = data)
    class Error<T>(errorMessage: UiText, data: T? = null): OhuneloResult<T>(data, errorMessage)
}
