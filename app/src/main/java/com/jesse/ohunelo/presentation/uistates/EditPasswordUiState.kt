package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class EditPasswordUiState(
    val password: String = "",
    val passwordError: UiText? = null,
    val isLoading: Boolean = false,
    val message: UiText? = null,
    val navigateBack: Boolean = false,
    val reauthenticate: Boolean = false
){
    // password is valid if it is not empty and passwordError is null
    fun isPasswordValid() = (password.isNotEmpty() && passwordError == null)
}
