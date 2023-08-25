package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.UiText
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor(){

    operator fun invoke(password: String): ValidationResult{
        if (password.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.this_field_cannot_be_blank)
            )
        }
        if(!isPasswordPatterValid(password)){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.password_must_contain)
            )
        }
        return ValidationResult(successful = true)
    }

    private fun isPasswordPatterValid(password: String): Boolean {
        /*
        * The regular expression pattern ^(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$ checks for the following conditions:
        ^: Start of the string.
        (?=.*[0-9]): Positive lookahead assertion to ensure at least one digit is present.
        (?=.*[^a-zA-Z0-9]): Positive lookahead assertion to ensure at least one non-alphanumeric character (symbol) is present.
        .{8,}: Match any character (except newline) at least 8 times.
        $: End of the string.
        * */
        val regexPattern = Regex("^(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$")
        return regexPattern.matches(password)
    }
}