package com.jesse.ohunelo.presentation.viewmodels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.network.signin_handlers.GoogleSignInHandler
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.presentation.uistates.EditEmailUiState
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UpdateStatus
import com.jesse.ohunelo.util.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditEmailViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val googleSignInHandler: GoogleSignInHandler
): ViewModel() {

    private val _editEmailUiState: MutableStateFlow<EditEmailUiState> = MutableStateFlow(
        EditEmailUiState()
    )
    val editEmailUiState get() = _editEmailUiState.asStateFlow()

    private var validationJob: Job? = null
    private val delayTime = 500L

    fun onEmailTextChanged(emailText: String){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(email = emailText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _editEmailUiState.update {
                    editEmailUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                editEmailUiState.copy(emailError = emailValidation.errorMessage)
            }
        }
    }

    fun reauthenticateTwitter(activity: Activity){
        viewModelScope.launch {
            when(val reauthenticateResult = authenticationRepository.reauthenticateTwitter(activity)){
                is OhuneloResult.Success -> {
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            message = reauthenticateResult.data,
                            isLoading = false
                        )
                    }
                }
                is OhuneloResult.Error -> {
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            isLoading = false,
                            message = reauthenticateResult.errorMessage
                        )
                    }
                }
            }
        }
    }

    fun startReauthenticateWithGoogle(onBeginSignInSuccess: (result: BeginSignInResult) -> Unit){
        googleSignInHandler.startSign(
            onSignInFailed = {
                    errorMessage ->
                _editEmailUiState.update {
                        editEmailUiState ->
                    editEmailUiState.copy(
                        isLoading = false,
                        message = errorMessage
                    )
                }
            },
            onBeginSignInSuccess = onBeginSignInSuccess
        )
    }

    fun finishReauthenticateWithGoogle(result: Intent?){
        viewModelScope.launch {
            when(val idTokenResult = googleSignInHandler.getSignInToken(result)){
                is OhuneloResult.Success -> {
                    when (val signInResult = authenticationRepository.reauthenticateGoogle(idTokenResult.data!!)){
                        is OhuneloResult.Success ->{
                            Timber.e("ViewModel SignIn with google Successful, user: ${signInResult.data}")
                            _editEmailUiState.update {
                                    editEmailUiState ->
                                editEmailUiState.copy(
                                    message = signInResult.data,
                                    isLoading = false
                                )
                            }
                        }
                        is OhuneloResult.Error -> {
                            _editEmailUiState.update {
                                    editEmailUiState ->
                                editEmailUiState.copy(
                                    message = signInResult.errorMessage,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
                is OhuneloResult.Error -> {
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            message = idTokenResult.errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun editEmail(){
            if(_editEmailUiState.value.isEmailValid()){
                viewModelScope.launch {
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            isLoading = true,
                        )
                    }
                when(val result = authenticationRepository.updateUserEmail(_editEmailUiState.value.email)){
                    is OhuneloResult.Success ->{
                        if (result.data == UpdateStatus.SUCCESS){
                          _editEmailUiState.update {
                                  editEmailUiState ->
                              editEmailUiState.copy(
                                  message = UiText.StringResource(R.string.edit_was_successful),
                                  navigateBack = true
                              )
                          }
                          return@launch
                        }
                    }
                    is OhuneloResult.Error -> {
                        if (result.data == UpdateStatus.REAUTHENTICATE){
                            _editEmailUiState.update {
                                    editEmailUiState ->
                                editEmailUiState.copy(
                                    message = result.errorMessage,
                                    reauthenticate = Pair(true, UserType.getUserType(authenticationRepository.getUserType()))
                                )
                            }
                            return@launch
                        }
                        _editEmailUiState.update {
                                editEmailUiState ->
                            editEmailUiState.copy(
                                message = result.errorMessage,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun renableButton(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                isLoading = false
            )
        }
    }

    fun onMessageShown(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                message = null
            )
        }
    }

    fun onReauthenticateInitiated(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                reauthenticate = Pair(false, null)
            )
        }
    }

    fun onFacebookReauthenticateFailed(errorMessage: UiText){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                isLoading = false,
                message = errorMessage
            )
        }
    }

    fun finishReauthenticateWithFacebook(idToken: String){
        viewModelScope.launch {
            when (val signInResult = authenticationRepository.reauthenticateFacebook(idToken)){
                is OhuneloResult.Success ->{
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            message = signInResult.data,
                            isLoading = false
                        )
                    }
                }
                is OhuneloResult.Error -> {
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            message = signInResult.errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onNavigateBack(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                navigateBack = false
            )
        }
    }
}