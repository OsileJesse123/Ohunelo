package com.jesse.ohunelo.data.network.service

import android.app.Activity
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UpdateStatus
import kotlinx.coroutines.flow.SharedFlow

interface AuthenticationService {

    val user: SharedFlow<AuthUser?>
    suspend fun registerUserWithEmailAndPassword(firstName: String, lastName: String, email: String, password: String): OhuneloResult<AuthUser>
    suspend fun loginUserWithEmailAndPassword(email: String, password: String): OhuneloResult<AuthUser>
    suspend fun logout()
    suspend fun verifyUserEmail(): OhuneloResult<Unit>
    suspend fun hasTheUserBeenVerified(): Boolean
    suspend fun sendPasswordResetEmail(email: String): OhuneloResult<Unit>
    suspend fun signInWithGoogle(idToken: String): OhuneloResult<AuthUser>
    suspend fun signInWithFacebook(idToken: String): OhuneloResult<AuthUser>
    suspend fun signInWithTwitter(activity: Activity): OhuneloResult<AuthUser>
    suspend fun updateTheUserName(firstName: String, lastName: String): OhuneloResult<Unit>
    suspend fun updateUserEmail(email: String): OhuneloResult<Unit>
    suspend fun updateUserPassword(password: String): OhuneloResult<Unit>
    suspend fun reauthenticateUserEmailPassword(email: String, password: String): OhuneloResult<Unit>
    suspend fun reauthenticateGoogle(idToken: String): OhuneloResult<Unit>
    suspend fun reauthenticateFacebook(accessToken: String): OhuneloResult<Unit>
    suspend fun reauthenticateTwitter(activity: Activity): OhuneloResult<Unit>

}