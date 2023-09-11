package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.util.UiText


sealed class OhuneloResult<out T: Any>{

    data class Success<out T: Any>(val data: T): OhuneloResult<T>()
    data class Error(val errorMessage: UiText): OhuneloResult<Nothing>()
}
