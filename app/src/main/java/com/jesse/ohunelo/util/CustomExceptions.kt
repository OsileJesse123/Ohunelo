package com.jesse.ohunelo.util

// Authentication Exceptions
sealed class AuthenticationException: Exception(){
    class NoUserException: AuthenticationException()
    class EmailAlreadyInUseException: AuthenticationException()
    class AccountExistWithDifferentCredentialException: AuthenticationException()
    class CredentialAlreadyInUseException: AuthenticationException()
    class InvalidCredentialsException: AuthenticationException()
    class UserDisabledException: AuthenticationException()
    class UserTokenExpired: AuthenticationException()
    class InvalidUserToken: AuthenticationException()
}

// Network Error Exception
class NetworkErrorException: Exception()

