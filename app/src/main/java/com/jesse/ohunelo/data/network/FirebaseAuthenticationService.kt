package com.jesse.ohunelo.data.network

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthenticationService @Inject constructor(): AuthenticationService {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun registerUserWithEmailAndPassword(firstName: String, lastName: String, email: String, password: String): OhuneloResult<AuthUser> {
        return try {

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null){
                updateUserName(user, "$firstName $lastName")
                // If registration task is successful and user is not null
               OhuneloResult.Success(AuthUser(
                   id = user.uid,
                   isEmailVerified =  user.isEmailVerified,
                   email = user.email,
                   userName = user.displayName
               ))
            } else {
                // If registration task is successful and user is null
                OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_registered_but_user_null))
            }
        }
        catch (authUserCollisionException: FirebaseAuthUserCollisionException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.email_already_in_use))
        }
        catch (firebaseException: FirebaseException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.internal_error_occured))
        }
        catch (e: Exception){
            Timber.e("Registration Failed, Exception: ($e), Message: (${e.message}) ")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.registration_failed))
        }
    }

    private suspend fun updateUserName(user: FirebaseUser, userName: String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(userName)
            .build()
        user.updateProfile(profileUpdates).await()
    }
}