package com.jesse.ohunelo.data.repository

import android.app.Activity
import com.jesse.ohunelo.data.local.PrefStore
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.AuthenticationService
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authenticationService: AuthenticationService,
    private val prefStore: PrefStore
): AuthenticationRepository {
    override suspend fun getUser(): AuthUser? {
        return withContext(ioDispatcher){
            authenticationService.getUser()
        }
    }

    override suspend fun registerUserWithEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): OhuneloResult<AuthUser> {
        return withContext(ioDispatcher){
            authenticationService.registerUserWithEmailAndPassword(
                firstName = firstName, lastName = lastName,
                email = email, password = password)
        }
    }

    override suspend fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): OhuneloResult<AuthUser> {
        return withContext(ioDispatcher){
            authenticationService.loginUserWithEmailAndPassword(email = email, password = password)
        }
    }

    override suspend fun logout() {
        withContext(ioDispatcher){
            authenticationService.logout()
            prefStore.isLoggedIn = false
        }
    }

    override suspend fun verifyUserEmail(): OhuneloResult<Boolean> {
        return withContext(ioDispatcher){
            authenticationService.verifyUserEmail()
        }
    }

    override suspend fun hasTheUserBeenVerified(): Boolean {
        return withContext(ioDispatcher){
            authenticationService.hasTheUserBeenVerified()
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): OhuneloResult<UiText> {
        return withContext(ioDispatcher){
            authenticationService.sendPasswordResetEmail(email)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): OhuneloResult<AuthUser> {
        return withContext(ioDispatcher){
            authenticationService.signInWithGoogle(idToken)
        }
    }

    override suspend fun signInWithFacebook(idToken: String): OhuneloResult<AuthUser> {
        return withContext(ioDispatcher){
            authenticationService.signInWithFacebook(idToken)
        }
    }

    override suspend fun signInWithTwitter(activity: Activity): OhuneloResult<AuthUser> {
        return withContext(ioDispatcher){
            authenticationService.signInWithTwitter(activity)
        }
    }

    override suspend fun updateTheUserName(firstName: String, lastName: String): OhuneloResult<Boolean> {
        return withContext(ioDispatcher){
            authenticationService.updateTheUserName(firstName, lastName)
        }
    }

    override suspend fun isAFirstTimeUser(): Boolean {
        return withContext(ioDispatcher){
            prefStore.isFirstTimeUser
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return withContext(ioDispatcher){
            prefStore.isLoggedIn
        }
    }

    override suspend fun updateIsAFirstTimeUser() {
        withContext(ioDispatcher){
            prefStore.isFirstTimeUser = false
        }
    }

    override suspend fun updateIsUserLoggedIn(isUserLoggedIn: Boolean) {
        withContext(ioDispatcher){
            prefStore.isLoggedIn = isUserLoggedIn
        }
    }
}