package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class ReauthenticateEmailUiState(
    val email: String = "",
    val password: String = "",
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val dismiss: Boolean = false,
    val message: UiText? = null,
    /**
     * This determines whether or not views in the Fragment should be enabled or not
     * **/
    val isEnabled: Boolean = true,
){
    fun isFormValid(): Boolean =
    // So long as email and password are not empty and emailError and passwordError are null then,
        // form is valid.
        (email.isNotBlank() && password.isNotBlank() && emailError == null && passwordError == null)

}
