package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class VerifyEmailUiState(
    val isEnabled: Boolean = true,
    val navigateToNextScreen: Boolean = false,
    /** first is a boolean determining whether or not an error message should be shown.
     *
     *  second is a nullable UiText, this is the error message to be displayed.
     * **/
    val showErrorMessage: Pair<Boolean, UiText?> = Pair(false, null)
)
