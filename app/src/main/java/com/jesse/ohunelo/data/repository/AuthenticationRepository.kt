package com.jesse.ohunelo.data.repository

import android.app.Activity
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.UiText

interface AuthenticationRepository {

    suspend fun getUser(): AuthUser?

    suspend fun registerUserWithEmailAndPassword(firstName: String, lastName: String, email: String, password: String): OhuneloResult<AuthUser>

    suspend fun loginUserWithEmailAndPassword(email: String, password: String): OhuneloResult<AuthUser>

    suspend fun logout()

    suspend fun verifyUserEmail(): OhuneloResult<Boolean>

    suspend fun hasTheUserBeenVerified(): Boolean

    suspend fun sendPasswordResetEmail(email: String): OhuneloResult<UiText>

    suspend fun signInWithGoogle(idToken: String): OhuneloResult<AuthUser>

    suspend fun signInWithFacebook(idToken: String): OhuneloResult<AuthUser>

    suspend fun signInWithTwitter(activity: Activity): OhuneloResult<AuthUser>
}