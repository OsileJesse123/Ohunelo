package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class UpdateProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val firstNameError: UiText? = null,
    val lastNameError: UiText? = null,
    val exitUpdateProfile: Boolean = false,
    /** first is a boolean determining whether or not an error message should be shown.
     *
     *  second is a nullable UiText, this is the error message to be displayed.
     * **/
    val showErrorMessage: Pair<Boolean, UiText?> = Pair(false, null),
    val showSuccessMessage: Pair<Boolean, UiText?> = Pair(false, null),
    /**
     * This determines whether or not the button in the Fragment should be enabled
     * **/
    val isEnabled: Boolean = false,
    val isLoading: Boolean = false,
){
    fun isFormValid(): Boolean =
    // So long as firstName, lastName, and password are not empty and firstNameError, lastNameError, and passwordError are null then,
        // form is valid.
        (firstName.isNotBlank() && lastName.isNotBlank() &&
                firstNameError == null && lastNameError == null)
}
