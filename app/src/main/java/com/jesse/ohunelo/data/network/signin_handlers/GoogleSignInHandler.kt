package com.jesse.ohunelo.data.network.signin_handlers

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class GoogleSignInHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(false)
                .build()
        ).build()

    fun startSign(
        onSignInFailed: (errorMessage: UiText) -> Unit,
        onBeginSignInSuccess: (result: BeginSignInResult) -> Unit
    ){
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener {
                result ->
            try {
                onBeginSignInSuccess(result)
            } catch (e: IntentSender.SendIntentException){
                onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                Timber.e("Couldn't start one tap ui: ${e.localizedMessage}")
            }
        }
        .addOnFailureListener {
                e ->
            when(e){
                is ApiException -> {
                    if (e.statusCode == 16){
                        Timber.e("Sign in with google failed,Exception: $e, Error Message: ${e.localizedMessage}")
                        onSignInFailed(UiText.StringResource(R.string.caller_temporarily_blocked))
                    } else{
                        Timber.e("Sign in with google failed,Exception: $e, Error Message: ${e.localizedMessage}")
                        onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                    }
                }
                else -> {
                    Timber.e("Sign in with google failed,Exception: $e, Error Message: ${e.localizedMessage}")
                    onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
                }
            }

        }
    }

    fun getSignInToken(data: Intent?): OhuneloResult<String>{
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if(idToken != null){
                OhuneloResult.Success(data = idToken)
            } else {
                Timber.e("IdToken is null")
                OhuneloResult.Error(UiText.StringResource(R.string.sign_in_failed, "Google"))
            }
        } catch (e: ApiException){
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Timber.e("One-tap dialog was closed.")
                    OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.sign_in_cancelled, "Google"))
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Timber.e("One-tap encountered a network error.")
                    // Try again or just ignore.
                    OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.network_error_occured))
                }
                else -> {
                    Timber.e("Couldn't get credential from result." +
                            " (${e.localizedMessage})")
                    OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.sign_in_failed, "Google"))
                }
            }
        }
    }

}