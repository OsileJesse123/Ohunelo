package com.jesse.ohunelo.domain.repository

import android.app.Activity
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import kotlinx.coroutines.flow.SharedFlow

interface AuthenticationRepository {

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

    suspend fun isAFirstTimeUser(): Boolean

    suspend fun isUserLoggedIn(): Boolean

    suspend fun updateIsAFirstTimeUser()

    suspend fun updateIsUserLoggedIn(isUserLoggedIn: Boolean)

    suspend fun updateUserEmail(email: String): OhuneloResult<Unit>

    suspend fun updateUserPassword(password: String): OhuneloResult<Unit>

    suspend fun reauthenticateUserEmailPassword(email: String, password: String): OhuneloResult<Unit>

    suspend fun reauthenticateGoogle(idToken: String): OhuneloResult<Unit>

    suspend fun reauthenticateFacebook(accessToken: String): OhuneloResult<Unit>

    suspend fun reauthenticateTwitter(activity: Activity): OhuneloResult<Unit>

    fun getUserType(): String?

}