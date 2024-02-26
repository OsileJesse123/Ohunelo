package com.jesse.ohunelo.data.network

import android.app.Activity
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.UiText

interface AuthenticationService {

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
    suspend fun updateTheUserName(firstName: String, lastName: String): OhuneloResult<Boolean>
    suspend fun updateUserEmail(email: String): OhuneloResult<Boolean>
    suspend fun reauthenticateUser()
}