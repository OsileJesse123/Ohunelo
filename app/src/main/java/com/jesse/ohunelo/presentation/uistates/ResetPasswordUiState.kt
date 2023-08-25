package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class ResetPasswordUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val showConfirmationMessage: Boolean = false
){
    fun isFormValid(): Boolean =
        // So long as email is not empty and emailError is null then form is valid.
        (email.isNotBlank() && emailError == null)
}
