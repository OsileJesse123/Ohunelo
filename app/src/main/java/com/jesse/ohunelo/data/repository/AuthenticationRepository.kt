package com.jesse.ohunelo.data.repository

import android.app.Activity
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthenticationRepository {

    val user: SharedFlow<AuthUser?>

    //suspend fun updateUser()

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

    suspend fun isAFirstTimeUser(): Boolean

    suspend fun isUserLoggedIn(): Boolean

    suspend fun updateIsAFirstTimeUser()

    suspend fun updateIsUserLoggedIn(isUserLoggedIn: Boolean)
}