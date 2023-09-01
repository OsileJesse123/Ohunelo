package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.UiText
import javax.inject.Inject

class ValidateUsernameUseCase @Inject constructor(){

    operator fun invoke(username: String): ValidationResult{
        if (username.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.this_field_cannot_be_blank)
            )
        }
        if (!isUsernamePatternValid(username)){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.this_field_cannot_be_blank)
            )
        }
        return ValidationResult(successful = true)
    }

    private fun isUsernamePatternValid(username: String): Boolean {
        /*
        * The regular expression pattern ^(?=\\S{8,16}$)(?!.*\\s{2,}).*\\S$ checks for the following conditions:
        ^: Start of the string.
        (?=\\S{8,16}$): Positive lookahead assertion to ensure the string has 8 to 16 non-whitespace characters.
        (?!.*\\s{2,}): Negative lookahead assertion to prevent more than one consecutive blank space.
        .*\\S: Match any character (except whitespace) at least once.
        $: End of the string.
        * */
        val regexPattern = Regex("^(?=\\S{8,16}$)(?!.*\\s{2,}).*\\S$")
        return regexPattern.matches(username)
    }
}