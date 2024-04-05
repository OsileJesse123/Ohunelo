package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.util.UiText

data class EditEmailUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isEnabled: Boolean = false,
    val message: UiText? = null,
    val navigateBack: Boolean = false
)
