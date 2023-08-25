package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.UiText
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): ValidationResult{

        if(email.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.this_field_cannot_be_blank)
            )
        }
        if(!isEmailPatterValid(email)){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.invalid_email)
            )
        }
        return ValidationResult(successful = true)
    }

    private fun isEmailPatterValid(email: String): Boolean{
        /*
        * The regular expression pattern ^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,4})+$ checks for the following conditions:
        It starts with one or more word characters (\\w+).
        It allows for zero or more groups of characters separated by a dot ([.-]?\\w+), such as . or -username.
        It requires the @ symbol.
        It allows for one or more word characters after the @ symbol (\\w+).
        It allows for zero or more groups of characters separated by a dot ([.-]?\\w+), such as .com or .co.uk.
        It ensures that the top-level domain has between 2 to 4 characters (\\.\\w{2,4}).
        It uses the ^ and $ anchors to match the entire string.
        * */
        val regexPattern = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,4})+$")
        return regexPattern.matches(email)
    }
}