package com.jesse.ohunelo.util

// Authentication Exceptions
sealed class AuthenticationException: Exception(){
    class NoUserException: AuthenticationException()
    class EmailAlreadyInUseException: AuthenticationException()
    class AccountExistWithDifferentCredentialException: AuthenticationException()
    class CredentialAlreadyInUseException: AuthenticationException()
    class InvalidCredentialsException: AuthenticationException()
    class UserDisabledException: AuthenticationException()
    class UserTokenExpiredException: AuthenticationException()
    class InvalidUserTokenException: AuthenticationException()
    class TooManyRequestsException: AuthenticationException()
    class AuthUserCollisionException: AuthenticationException()
    class AuthRecentLoginRequiredException: AuthenticationException()
    class WeakPasswordException: AuthenticationException()
    class InvalidUserException: AuthenticationException()
    class SignInCancelledException: AuthenticationException()
}

// Network Error Exception
class NetworkErrorException: Exception()

// Unauthorized Exception
class UnauthorizedException: Exception()

// Not Found Exception
class NotFoundException: Exception()

// Rate Limit Exceeded Exception
class RateLimitExceededException: Exception()

// Server Error Exception
class ServerErrorException: Exception()

