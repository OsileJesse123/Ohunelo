package com.jesse.ohunelo.data.model

data class AuthUser(
    val id: String,
    val isEmailVerified: Boolean,
    val email: String?,
    val userName: String?
)
