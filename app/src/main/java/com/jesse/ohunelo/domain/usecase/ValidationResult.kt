package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.util.UiText

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null
)
