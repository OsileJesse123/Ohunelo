package com.jesse.ohunelo.presentation.viewmodels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.jesse.ohunelo.data.model.AuthUser
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.network.signin_handlers.FacebookSignInHandler
import com.jesse.ohunelo.data.network.signin_handlers.GoogleSignInHandler
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.LoginUiState
import com.jesse.ohunelo.util.HOME_FRAGMENT
import com.jesse.ohunelo.util.UPDATE_USERNAME_FRAGMENT
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.VERIFY_EMAIL_FRAGMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val googleSignInHandler: GoogleSignInHandler,
    private val facebookSignInHandler: FacebookSignInHandler
): ViewModel() {

    private val _loginUiStateFlow: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiStateFlow: StateFlow<LoginUiState> get() = _loginUiStateFlow.asStateFlow()

    // This is to ensure that validation of text in EditText is optimal. When user enters text,
    // validation doesn't happen immediately, initial task is canceled and restarted then there is
    // a delay of 500 milliseconds (to ensure that user has finished typing or not) before validation
    // actually occurs and UI state is updated.
    private var validationJob: Job? = null

    private val delayTime = 500L

    fun onEmailTextChanged(emailText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _loginUiStateFlow.update {
                    loginUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                loginUiState.copy(
                    email = emailText,
                    emailError = emailValidation.errorMessage
                )
            }
        }
    }

    fun onPasswordTextChanged(passwordText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _loginUiStateFlow.update {
                    loginUiState ->
                val passwordValidation = validatePasswordUseCase(password = passwordText,
                    shouldValidatePasswordPattern = false)
                loginUiState.copy(
                    password = passwordText,
                    passwordError = passwordValidation.errorMessage
                )
            }
        }
    }

    fun login(){
        viewModelScope.launch {
            if(_loginUiStateFlow.value.isFormValid()){
                // Disable buttons and show loader in UI
                _loginUiStateFlow.update {
                        loginUiState ->
                    loginUiState.copy(
                        isEnabled = false
                    )
                }
                val loginResult = authenticationRepository.loginUserWithEmailAndPassword(
                    email = _loginUiStateFlow.value.email,
                    password = _loginUiStateFlow.value.password
                )
                when(loginResult){
                    is OhuneloResult.Success -> {
                        Timber.e("ViewModel Login Successful, user: ${loginResult.data}")
                        _loginUiStateFlow.update {
                                loginUiState ->
                            loginUiState.copy(
                                navigateToNextScreen = determineNavigationDestination(loginResult.data),
                                isEnabled = true
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        _loginUiStateFlow.update {
                                loginUiState ->
                            loginUiState.copy(
                                showErrorMessage = Pair(true, loginResult.errorMessage),
                                isEnabled = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun startSignInWithGoogle(onBeginSignInSuccess: (result: BeginSignInResult) -> Unit){
        startSignIn()
        googleSignInHandler.startSign(
            onSignInFailed = {
                errorMessage ->
                _loginUiStateFlow.update {
                        loginUiState ->
                    loginUiState.copy(
                        isEnabled = true,
                        showErrorMessage = Pair(true, errorMessage)
                    )
                }
            },
            onBeginSignInSuccess = onBeginSignInSuccess
        )
    }

    fun finishSignInWithGoogle(result: Intent?){
        viewModelScope.launch {
            when(val idTokenResult = googleSignInHandler.getSignInToken(result)){
                is OhuneloResult.Success -> {
                    when (val signInResult = authenticationRepository.signInWithGoogle(idTokenResult.data!!)){
                        is OhuneloResult.Success ->{
                            Timber.e("ViewModel SignIn with google Successful, user: ${signInResult.data}")
                            _loginUiStateFlow.update {
                                    loginUiState ->
                                loginUiState.copy(
                                    navigateToNextScreen = determineNavigationDestination(signInResult.data),
                                )
                            }
                        }
                        is OhuneloResult.Error -> {
                            _loginUiStateFlow.update {
                                    loginUiState ->
                                loginUiState.copy(
                                    showErrorMessage = Pair(true, signInResult.errorMessage),
                                    isEnabled = true
                                )
                            }
                        }
                    }
                }
                is OhuneloResult.Error -> {
                    _loginUiStateFlow.update {
                            loginUiState ->
                        loginUiState.copy(
                            showErrorMessage = Pair(true, idTokenResult.errorMessage),
                            isEnabled = true
                        )
                    }
                }
            }
        }
    }

    fun signInWithTwitter(activity: Activity){
        viewModelScope.launch {
            // Disable buttons and show loader in UI
            _loginUiStateFlow.update {
                    loginUiState ->
                loginUiState.copy(
                    isEnabled = false
                )
            }

            when(val signInResult = authenticationRepository.signInWithTwitter(activity)){
                is OhuneloResult.Success -> {
                    Timber.e("ViewModel Sign in with Twitter Successful, user: ${signInResult.data}")
                    _loginUiStateFlow.update {
                            loginUiState ->
                        loginUiState.copy(
                            navigateToNextScreen = determineNavigationDestination(signInResult.data),
                            isEnabled = true
                        )
                    }
                }
                is OhuneloResult.Error -> {
                    _loginUiStateFlow.update {
                            loginUiState ->
                        loginUiState.copy(
                            showErrorMessage = Pair(true, signInResult.errorMessage),
                            isEnabled = true
                        )
                    }
                }
            }
        }
    }

    fun finishSignInWithFacebook(idToken: String){
        viewModelScope.launch {
            when (val signInResult = authenticationRepository.signInWithFacebook(idToken)){
                is OhuneloResult.Success ->{
                    Timber.e("ViewModel SignIn with facebook Successful, user: ${signInResult.data}")
                    _loginUiStateFlow.update {
                            loginUiState ->
                        loginUiState.copy(
                            navigateToNextScreen = determineNavigationDestination(signInResult.data),
                            isEnabled = true
                        )
                    }
                }
                is OhuneloResult.Error -> {
                    _loginUiStateFlow.update {
                            loginUiState ->
                        loginUiState.copy(
                            showErrorMessage = Pair(true, signInResult.errorMessage),
                            isEnabled = true
                        )
                    }
                }
            }
        }
    }

    fun startSignIn(){
        // Disable buttons and show loader in UI
        _loginUiStateFlow.update {
                loginUiState ->
            loginUiState.copy(
                isEnabled = false
            )
        }
    }

    fun onSignInFailed(errorMessage: UiText){
        _loginUiStateFlow.update {
                loginUiState ->
            loginUiState.copy(
                isEnabled = true,
                showErrorMessage = Pair(true, errorMessage)
            )
        }
    }

    fun onErrorMessageShown(){
        _loginUiStateFlow.update {
                loginUiStateFlow ->
            loginUiStateFlow.copy(
                showErrorMessage = Pair(false, null)
            )
        }
    }

    fun onNavigationToNextScreen(){
        viewModelScope.launch {
            _loginUiStateFlow.update {
                    loginUiStateFlow ->
                loginUiStateFlow.copy(
                    navigateToNextScreen = Pair(false, ""),
                    isEnabled = true,
                )
            }
        }
    }

    private fun updateIsUserLoggedIn(){
        viewModelScope.launch {
            authenticationRepository.updateIsUserLoggedIn(true)
        }
    }

    private fun determineNavigationDestination(user: AuthUser?): Pair<Boolean, String>{
        return if(user != null){
           // If the user is not null determine where to navigate to
           if(user.isEmailVerified){
               // If the user's name is not null then navigate to the home screen
               if(user.userName != null){
                   // User logged in successfully so update status
                   updateIsUserLoggedIn()
                   Pair(true, HOME_FRAGMENT)
               } else {
                   // If the user's name is null then navigate to the update user name screen
                   Pair(true, UPDATE_USERNAME_FRAGMENT)
               }
               // If the user's email is verified then navigate to the home screen
               // User logged in successfully so update status
               updateIsUserLoggedIn()
               Pair(true, HOME_FRAGMENT)
           } else{
               // else navigate to the verify email screen
               Pair(true, VERIFY_EMAIL_FRAGMENT)
           }
        } else{
            // else don't bother navigating at all
            Pair(false, "")
        }
    }
}