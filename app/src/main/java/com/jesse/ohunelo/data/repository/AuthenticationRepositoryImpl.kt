package com.jesse.ohunelo.data.repository

import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.AuthenticationService
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authenticationService: AuthenticationService
): AuthenticationRepository {

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
}