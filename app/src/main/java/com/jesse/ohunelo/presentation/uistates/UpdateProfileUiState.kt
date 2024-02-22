package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.util.UiText

data class UpdateProfileUiState(
    val user: AuthUser? = null,
    val userName: String = "",
    val email: String = "",
    val userNameError: UiText? = null,
    val emailError: UiText? = null,
    /** first is a boolean determining whether or not an error message should be shown.
     *
     *  second is a nullable UiText, this is the error message to be displayed.
     * **/
    val showErrorMessage: Pair<Boolean, UiText?> = Pair(false, null),
    /**
     * This determines whether or not views in the Fragment should be enabled or not
     * **/
    val isEnabled: Boolean = true,
)
