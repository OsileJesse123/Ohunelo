package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val firstNameError: UiText? = null,
    val lastNameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val navigateToNextScreen: Boolean = false
){
    fun isFormValid(): Boolean =
    // So long as firstName, lastName, email and password are not empty and firstNameError, lastNameError, emailError and passwordError are null then,
        // form is valid.
        (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                firstNameError == null && lastNameError == null && emailError == null && passwordError == null)
}
