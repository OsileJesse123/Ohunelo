package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class UpdateUsernameUiState(
    val firstName: String = "",
    val lastName: String = "",
    val firstNameError: UiText? = null,
    val lastNameError: UiText? = null,
    val navigateToNextScreen: Boolean = false,
    /** first is a boolean determining whether or not an error message should be shown.
     *
     *  second is a nullable UiText, this is the error message to be displayed.
     * **/
    val showErrorMessage: Pair<Boolean, UiText?> = Pair(false, null),
    /**
     * This determines whether or not views in the Fragment should be enabled or not
     * **/
    val isEnabled: Boolean = true,
){
    fun isFormValid(): Boolean =
    // So long as firstName, lastName are not empty and firstNameError, lastNameError are null then, form is valid.
        (firstName.isNotBlank() && lastName.isNotBlank() && firstNameError == null && lastNameError == null )
}
