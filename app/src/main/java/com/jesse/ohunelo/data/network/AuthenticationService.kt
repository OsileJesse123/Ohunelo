package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult

interface AuthenticationService {

    suspend fun getUser(): AuthUser?
    suspend fun registerUserWithEmailAndPassword(firstName: String, lastName: String, email: String, password: String): OhuneloResult<AuthUser>
    suspend fun loginUserWithEmailAndPassword(email: String, password: String): OhuneloResult<AuthUser>
    suspend fun logout()
    suspend fun verifyUserEmail(): OhuneloResult<Boolean>
    suspend fun hasTheUserBeenVerified(): Boolean
}