package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UserType

data class EditEmailUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isLoading: Boolean = false,
    val message: UiText? = null,
    val reauthenticate: Pair<Boolean, UserType?> = Pair(false, null),
    val logout: Boolean = false
){
    // email is valid if it is not empty and emailError is null
    fun isEmailValid() = (email.isNotEmpty() && emailError == null)
}
