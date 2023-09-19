package com.jesse.ohunelo.data.network

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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
    override suspend fun getUser(): AuthUser? {
        firebaseAuth.sendPasswordResetEmail("")
        return firebaseAuth.currentUser?.let {
            firebaseUser ->
            AuthUser(
                id = firebaseUser.uid,
                isEmailVerified =  firebaseUser.isEmailVerified,
                email = firebaseUser.email,
                userName = firebaseUser.displayName
            )
        }
    }

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
        catch (e: FirebaseAuthUserCollisionException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.email_already_in_use))
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.network_error_occured))
        }
        catch (e: Exception){
            Timber.e("Registration Failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.registration_failed))
        }
    }

    override suspend fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): OhuneloResult<AuthUser> {
        return try{
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null){
                // If login task is successful and user is not null
                OhuneloResult.Success(AuthUser(
                    id = user.uid,
                    isEmailVerified =  user.isEmailVerified,
                    email = user.email,
                    userName = user.displayName
                ))
            } else {
                // If login task is successful and user is null
                OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_logged_in_but_user_null))
            }
        }
        catch (e: FirebaseAuthInvalidCredentialsException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.invalid_credentials))
        }
        catch (e: FirebaseAuthInvalidUserException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.invalid_user))
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.network_error_occured))
        }
        catch (e: Exception){
            Timber.e("Login Failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.login_failed))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun verifyUserEmail(): OhuneloResult<Boolean> {
        return try{
            val user = firebaseAuth.currentUser
            if (user != null){
                user.sendEmailVerification().await()
                OhuneloResult.Success(true)
            } else{
                OhuneloResult.Error(UiText.StringResource(R.string.send_email_link_failed))
            }
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(UiText.StringResource(R.string.network_error_occured))
        }
        catch (e: FirebaseTooManyRequestsException){
            OhuneloResult.Error(UiText.StringResource(R.string.too_many_requests))
        }
        catch (e: Exception){
            Timber.e("Send email verification failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.send_email_link_failed))
        }
    }

    override suspend fun hasTheUserBeenVerified(): Boolean {
        return try {
            val user = firebaseAuth.currentUser
            if(user != null){
                // If the user is not null reload the user to get an updated status of the user then
                // check to see if user's email is verified.
                user.reload().await()
                user.isEmailVerified
            } else{
                // Else the user is null hence has not been verified
                false
            }
        } catch (e: Exception){
            Timber.e("Has user been verified failed, Exception: $e")
            false
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): OhuneloResult<UiText> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            OhuneloResult.Success(UiText.StringResource(R.string.reset_password_email))
        }
        catch (e: FirebaseAuthInvalidUserException){
            OhuneloResult.Error(UiText.StringResource(R.string.no_user_record_corresponding))
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(UiText.StringResource(R.string.network_error_occured))
        }
        catch (e: Exception){
            Timber.e("Has user been verified failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.reset_password_email_failed))
        }
    }

    private suspend fun updateUserName(user: FirebaseUser, userName: String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(userName)
            .build()
        user.updateProfile(profileUpdates).await()
    }
}