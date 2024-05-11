package com.jesse.ohunelo.domain

import com.jesse.ohunelo.util.UiText

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null
)
