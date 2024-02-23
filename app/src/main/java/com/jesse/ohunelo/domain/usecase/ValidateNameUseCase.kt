package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.UiText
import javax.inject.Inject

class ValidateNameUseCase @Inject constructor(){

    operator fun invoke(username: String, nameMaxLength: Int): ValidationResult{
        if (username.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.this_field_cannot_be_blank)
            )
        }
        if (!isUsernamePatternValid(username, nameMaxLength)){
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.name_should, nameMaxLength)
            )
        }
        return ValidationResult(successful = true)
    }

    private fun isUsernamePatternValid(username: String, nameMaxLength: Int): Boolean {
        /*
        * ^ and $: These symbols indicate the start and end of the string, respectively, ensuring that the regex matches the entire string.
          [a-zA-Z]: This part allows only letters, both uppercase and lowercase. No numbers or symbols are allowed.
          {3,8}: This specifies that the username should be between 3 and 8 characters long.
          So, this regex pattern enforces your criteria for a username: it's no longer than 8
          characters, doesn't allow blank spaces, and only allows letters (no numbers or symbols).
        * */
        val regexPattern = Regex("^[a-zA-Z]{3,$nameMaxLength}\$")
        return regexPattern.matches(username)
    }
}