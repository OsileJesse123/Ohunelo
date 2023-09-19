package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class ResetPasswordUiState(
    val email: String = "",
    val emailError: UiText? = null,
    /** first is a boolean determining whether or not an confirmation message should be shown.
     *
     *  second is a nullable UiText, this is the confirmation message to be displayed.
     * **/
    val showConfirmationMessage: Pair<Boolean, UiText?> = Pair(false, null),
    /** first is a boolean determining whether or not an error message should be shown.
     *
     *  second is a nullable UiText, this is the error message to be displayed.
     * **/
    val showErrorMessage: Pair<Boolean, UiText?> = Pair(false, null),
    val isEnabled: Boolean = true
){
    fun isFormValid(): Boolean =
        // So long as email is not empty and emailError is null then form is valid.
        (email.isNotBlank() && emailError == null)
}
