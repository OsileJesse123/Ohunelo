package com.jesse.ohunelo.util

import android.util.Patterns
import javax.inject.Inject

/** This was done to abstract the android.util.Patterns keeping the ValidateEmailUseCase
 testable.**/
interface EmailMatcher{
    fun emailMatches(email: String): Boolean
}

class EmailMatcherImpl @Inject constructor(): EmailMatcher{
    override fun emailMatches(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}