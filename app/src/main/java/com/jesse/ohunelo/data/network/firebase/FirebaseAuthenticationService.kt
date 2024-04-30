package com.jesse.ohunelo.data.network.firebase

import android.app.Activity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.local.PrefStore
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.AuthenticationService
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.SPLIT_FIRST_AND_LAST_NAME_WITH_WHITESPACE
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UpdateStatus
import com.jesse.ohunelo.util.UserType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthenticationService @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val prefStore: PrefStore
): AuthenticationService {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user: MutableSharedFlow<AuthUser?> = MutableSharedFlow()
    override val user: SharedFlow<AuthUser?> = _user.shareIn(
        CoroutineScope(ioDispatcher),
        started = SharingStarted.Lazily,
        replay = 1
    ).onSubscription {
        emit(getUser())
    }

    private fun getUser(): AuthUser? {
        Timber.e("Service User: ${firebaseAuth.currentUser}")
        return firebaseAuth.currentUser?.let {
            firebaseUser ->
            AuthUser(
                id = firebaseUser.uid,
                isEmailVerified =  firebaseUser.isEmailVerified,
                email = firebaseUser.email,
                userName = firebaseUser.displayName
            )
        }
    }

    override suspend fun registerUserWithEmailAndPassword(firstName: String, lastName: String, email: String, password: String): OhuneloResult<AuthUser> {
        return try {

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null){
                updateUserName(user, "$firstName$SPLIT_FIRST_AND_LAST_NAME_WITH_WHITESPACE$lastName")
                // If registration task is successful and user is not null
                val authUser = AuthUser(
                    id = user.uid,
                    isEmailVerified =  user.isEmailVerified,
                    email = user.email,
                    userName = user.displayName
                )
                _user.emit(authUser)
               OhuneloResult.Success(authUser)
            } else {
                // If registration task is successful and user is null
                OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_registered_but_user_null))
            }
        }
        catch (e: FirebaseAuthUserCollisionException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.email_already_in_use))
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.network_error_occured))
        }
        catch (e: Exception){
            Timber.e("Registration Failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.registration_failed))
        }
    }

    override suspend fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): OhuneloResult<AuthUser> {
        return try{
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null){
                // If login task is successful and user is not null
                val authUser = AuthUser(
                    id = user.uid,
                    isEmailVerified =  user.isEmailVerified,
                    email = user.email,
                    userName = user.displayName
                )
                _user.emit(authUser)
                prefStore.userType = UserType.EMAIL_PASSWORD.userType
                OhuneloResult.Success(authUser)
            } else {
                // If login task is successful and user is null
                OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_logged_in_but_user_null))
            }
        }
        catch (e: FirebaseAuthInvalidCredentialsException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.invalid_credentials))
        }
        catch (e: FirebaseAuthInvalidUserException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.invalid_user))
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.network_error_occured))
        }
        catch (e: Exception){
            Timber.e("Login Failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.login_failed))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        prefStore.apply {
            isLoggedIn = false
            userType = null
        }
        _user.emit(null)
    }

    override suspend fun verifyUserEmail(): OhuneloResult<Unit> {
        return try{
            val user = firebaseAuth.currentUser
            if (user != null){
                user.sendEmailVerification().await()
                OhuneloResult.Success(Unit)
            } else{
                OhuneloResult.Error(UiText.StringResource(R.string.send_email_link_failed))
            }
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(UiText.StringResource(R.string.network_error_occured))
        }
        catch (e: FirebaseTooManyRequestsException){
            OhuneloResult.Error(UiText.StringResource(R.string.too_many_requests))
        }
        catch (e: Exception){
            Timber.e("Send email verification failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.send_email_link_failed))
        }
    }

    override suspend fun hasTheUserBeenVerified(): Boolean {
        return try {
            val user = firebaseAuth.currentUser
            if(user != null){
                // If the user is not null reload the user to get an updated status of the user then
                // check to see if user's email is verified.
                user.reload().await()
                user.isEmailVerified
            } else{
                // Else the user is null hence has not been verified
                false
            }
        } catch (e: Exception){
            Timber.e("Has user been verified failed, Exception: $e")
            false
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): OhuneloResult<UiText> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            OhuneloResult.Success(UiText.StringResource(R.string.reset_password_email))
        }
        catch (e: FirebaseAuthInvalidUserException){
            OhuneloResult.Error(UiText.StringResource(R.string.no_user_record_corresponding))
        }
        catch (e: FirebaseNetworkException){
            OhuneloResult.Error(UiText.StringResource(R.string.network_error_occured))
        }
        catch (e: Exception){
            Timber.e("Has user been verified failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.reset_password_email_failed))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): OhuneloResult<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null){
                // If sign in task is successful and user is not null
                val authUser = AuthUser(
                    id = user.uid,
                    isEmailVerified =  user.isEmailVerified,
                    email = user.email,
                    userName = user.displayName
                )
                _user.emit(authUser)
                prefStore.userType = UserType.GOOGLE.userType
                OhuneloResult.Success(authUser)
            } else {
                // If login task is successful and user is null
                OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_logged_in_but_user_null))
            }
        } catch (e: Exception){
            Timber.e("Sign in with google failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.sign_in_failed, "Google"))
        }
    }

    override suspend fun signInWithFacebook(idToken: String): OhuneloResult<AuthUser> {
        return try {
            val credential = FacebookAuthProvider.getCredential(idToken)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null){
                // If sign in task is successful and user is not null
                val authUser = AuthUser(
                    id = user.uid,
                    isEmailVerified =  user.isEmailVerified,
                    email = user.email,
                    userName = user.displayName
                )
                prefStore.userType = UserType.FACEBOOK.userType
                _user.emit(authUser)
                OhuneloResult.Success(authUser)
            } else {
                // If login task is successful and user is null
                OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_logged_in_but_user_null))
            }
        } catch (e: FirebaseAuthUserCollisionException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.email_already_in_use))
        } catch (e: Exception){
            Timber.e("Sign in with facebook failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.sign_in_failed, "Facebook"))
        }
    }

    override suspend fun signInWithTwitter(activity: Activity): OhuneloResult<AuthUser> {
        val provider = OAuthProvider.newBuilder("twitter.com")

        return try {
            val pendingResultTask = firebaseAuth.pendingAuthResult

            if(pendingResultTask != null){
                // There's something already here! Finish the sign-in for your user.
                val user = pendingResultTask.await().user

                if (user != null){
                    // If sign in task is successful and user is not null
                    val authUser = AuthUser(
                        id = user.uid,
                        isEmailVerified =  user.isEmailVerified,
                        email = user.email,
                        userName = user.displayName
                    )
                    prefStore.userType = UserType.TWITTER.userType
                    _user.emit(authUser)
                    OhuneloResult.Success(authUser)
                } else {
                    // If login task is successful and user is null
                    OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_logged_in_but_user_null))
                }
            } else {
                // There's no pending result so you need to start the sign-in flow.
                val result = firebaseAuth
                    .startActivityForSignInWithProvider(activity, provider.build()).await()
                val user = result.user

                if (user != null){
                    // If sign in task is successful and user is not null
                    val authUser = AuthUser(
                        id = user.uid,
                        isEmailVerified =  user.isEmailVerified,
                        email = user.email,
                        userName = user.displayName
                    )
                    prefStore.userType = UserType.TWITTER.userType
                    _user.emit(authUser)
                    OhuneloResult.Success(authUser)
                } else {
                    // If login task is successful and user is null
                    OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_logged_in_but_user_null))
                }
            }
        } catch (e: FirebaseAuthUserCollisionException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.email_already_in_use))
        } catch (e: Exception){
            Timber.e("Sign in with twitter failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.sign_in_failed, "Twitter"))
        }
    }

    override suspend fun updateTheUserName(firstName: String, lastName: String): OhuneloResult<Unit> {
        return try {
            val user = firebaseAuth.currentUser

            if(user != null){
                // If user is not null, update the user name
                updateUserName(user, "$firstName $lastName")
                _user.emit(AuthUser(
                    id = user.uid,
                    isEmailVerified =  user.isEmailVerified,
                    email = user.email,
                    userName = user.displayName
                ))
                OhuneloResult.Success(Unit)
            } else{
                // If user is null, no user was found so no update
                OhuneloResult.Error(UiText.StringResource(R.string.no_user_found))
            }
        } catch (e: Exception){
            Timber.e("Username update failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(resId = R.string.user_name_update_failed))
        }
    }

    override suspend fun updateUserEmail(email: String): OhuneloResult<UpdateStatus> {
        return try{
            firebaseAuth.currentUser?.let {
                    user ->
                user.updateEmail(email).await()
                _user.emit(getUser())
                (OhuneloResult.Success(data = UpdateStatus.SUCCESS))
            } ?: OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.edit_email_failed))
        } catch (e: FirebaseAuthUserCollisionException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.email_already_in_use))
        } catch (e: FirebaseAuthRecentLoginRequiredException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.reauthenticate_message), data = UpdateStatus.REAUTHENTICATE)
        } catch (e: Exception){
            Timber.e("Update email failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.edit_email_failed))
        }
    }

    override suspend fun updateUserPassword(password: String): OhuneloResult<UpdateStatus> {
        return try {
            firebaseAuth.currentUser?.let {
                user ->
                user.updatePassword(password).await()
                (OhuneloResult.Success(data = UpdateStatus.SUCCESS))
            } ?: OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.edit_password_failed))
        }
        catch (e: FirebaseAuthWeakPasswordException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.weak_password))
        }
        catch (e: FirebaseAuthRecentLoginRequiredException){
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.reauthenticate_message), data = UpdateStatus.REAUTHENTICATE)
        }
        catch (e: FirebaseAuthInvalidUserException){
            Timber.e("Update password failed 2, Exception: $e, Cause: ${e.cause}, ErrorCode: ${e.errorCode}")
            when(e.errorCode){
                FirebaseErrorCode.ERROR_USER_TOKEN_EXPIRED.name -> {
                    OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.user_credential_no_longer_valid))
                }
                else -> {
                    OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.no_user_record_corresponding))
                }
            }
        }
        catch (e: Exception){
            Timber.e("Update password failed, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.edit_password_failed))
        }
    }

    override suspend fun reauthenticateUserEmailPassword(
        email: String,
        password: String
    ): OhuneloResult<UiText> {
        return try {
            firebaseAuth.currentUser?.let {
                user ->
                val credential = EmailAuthProvider.getCredential(
                    email, password
                )
                user.reauthenticate(credential).await()
                OhuneloResult.Success(UiText.StringResource(R.string.reauthenticate_success))
            } ?: OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))

        }
        catch (e: FirebaseAuthInvalidCredentialsException){
            Timber.e("Re-authenticate with email/password failed 1, Exception: $e")
            OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.invalid_credentials))
        }
        catch (e: FirebaseAuthInvalidUserException){
            OhuneloResult.Error(UiText.StringResource(R.string.no_user_record_corresponding))
        }
        catch (e: Exception){
            Timber.e("Re-authenticate with email/password failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        }
    }

    override suspend fun reauthenticateGoogle(idToken: String): OhuneloResult<UiText> {
        return try {
            firebaseAuth.currentUser?.let {
                    user ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                user.reauthenticate(credential).await()
                OhuneloResult.Success(UiText.StringResource(R.string.reauthenticate_success))
            } ?:OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        } catch (e: Exception){
            Timber.e("Re-authenticate with google failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        }
    }

    override suspend fun reauthenticateFacebook(accessToken: String): OhuneloResult<UiText> {
        return try {
            firebaseAuth.currentUser?.let {
                    user ->
                val credential = FacebookAuthProvider.getCredential(accessToken)
                user.reauthenticate(credential).await()
                OhuneloResult.Success(UiText.StringResource(R.string.reauthenticate_success))
            } ?: OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        } catch (e: Exception){
            Timber.e("Re-authenticate with facebook failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        }
    }

    override suspend fun reauthenticateTwitter(activity: Activity): OhuneloResult<UiText> {
        return try {
            val provider = OAuthProvider.newBuilder("twitter.com")
            val pendingResultTask = firebaseAuth.pendingAuthResult
            val credential = if(pendingResultTask != null){
                pendingResultTask.await().credential
            } else {
                firebaseAuth
                    .startActivityForSignInWithProvider(activity, provider.build()).await().credential
            }
            firebaseAuth.currentUser?.let {
                    user ->
                if (credential != null) {
                    user.reauthenticate(credential).await()
                    OhuneloResult.Success(UiText.StringResource(R.string.reauthenticate_success))
                } else {
                    OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
                }
            } ?: OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        } catch (e: Exception){
            Timber.e("Re-authenticate with twitter failed, Exception: $e")
            OhuneloResult.Error(UiText.StringResource(R.string.reauthenticate_fail))
        }
    }

    private suspend fun updateUserName(user: FirebaseUser, userName: String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(userName)
            .build()
         user.updateProfile(profileUpdates).await()
    }
}