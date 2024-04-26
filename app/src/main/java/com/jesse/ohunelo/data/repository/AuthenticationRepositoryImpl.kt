package com.jesse.ohunelo.data.repository

import android.app.Activity
import com.jesse.ohunelo.data.local.PrefStore
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.AuthenticationService
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UpdateStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authenticationService: AuthenticationService,
    private val prefStore: PrefStore
): AuthenticationRepository {

    override val user = authenticationService.user

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
        }
    }

    override suspend fun verifyUserEmail(): OhuneloResult<Unit> {
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

    override suspend fun updateTheUserName(firstName: String, lastName: String): OhuneloResult<Unit> {
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

    override suspend fun reauthenticateUserEmailPassword(
        email: String,
        password: String
    ): OhuneloResult<UiText> {
        return withContext(ioDispatcher){
            authenticationService.reauthenticateUserEmailPassword(email, password)
        }
    }

    override suspend fun reauthenticateGoogle(idToken: String): OhuneloResult<UiText> {
        return withContext(ioDispatcher){
            authenticationService.reauthenticateGoogle(idToken)
        }
    }

    override suspend fun reauthenticateFacebook(accessToken: String): OhuneloResult<UiText> {
        return withContext(ioDispatcher){
            authenticationService.reauthenticateFacebook(accessToken)
        }
    }

    override suspend fun reauthenticateTwitter(
        activity: Activity
    ): OhuneloResult<UiText> {
        return withContext(ioDispatcher){
            authenticationService.reauthenticateTwitter(activity)
        }
    }

    override fun getUserType(): String? {
        return prefStore.userType

    }

    override suspend fun updateUserEmail(email: String): OhuneloResult<UpdateStatus> {
        return withContext(ioDispatcher){
            authenticationService.updateUserEmail(email)
        }
    }
}