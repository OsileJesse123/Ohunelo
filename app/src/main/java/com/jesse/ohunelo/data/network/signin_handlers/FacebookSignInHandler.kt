package com.jesse.ohunelo.data.network.signin_handlers

import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.UiText
import timber.log.Timber
import javax.inject.Inject


class FacebookSignInHandler @Inject constructor(){

    private val loginManager: LoginManager = LoginManager.getInstance()
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    fun signIn(
        onSignInSuccess: (idToken: String) -> Unit,
        onSignInFailed: (errorMessage: UiText) -> Unit,
        fragment: Fragment
    ){
        loginManager.logInWithReadPermissions(
            fragment,
            callbackManager,
            listOf( "email", "public_profile")
        )
        loginManager.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                onSignInSuccess(result.accessToken.token)
            }

            override fun onCancel() {
                onSignInFailed(UiText.StringResource(R.string.sign_in_cancelled, "Facebook"))
            }

            override fun onError(error: FacebookException) {
                Timber.e("Login with facebook failed, Error: ${error.message}")
                onSignInFailed(UiText.StringResource(R.string.sign_in_failed, "Google"))
            }
        })
    }
}