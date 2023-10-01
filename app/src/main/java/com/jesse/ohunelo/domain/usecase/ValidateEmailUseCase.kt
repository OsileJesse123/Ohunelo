package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.EmailMatcher
import com.jesse.ohunelo.util.UiText
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(
    private val emailMatcher: EmailMatcher
) {

    operator fun invoke(email: String): ValidationResult{

        if(email.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.this_field_cannot_be_blank)
            )
        }
        if(!emailMatcher.emailMatches(email)){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.invalid_email)
            )
        }
        return ValidationResult(successful = true)
    }


}