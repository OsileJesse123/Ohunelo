package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.ReauthenticateEmailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReauthenticateEmailViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,

    ): ViewModel() {

    private val _reauthenticateEmailUiState: MutableStateFlow<ReauthenticateEmailUiState> =
        MutableStateFlow(ReauthenticateEmailUiState())
    val reauthenticateEmailUiState get() = _reauthenticateEmailUiState.asStateFlow()

    // This is to ensure that validation of text in EditText is optimal. When user enters text,
    // validation doesn't happen immediately, initial task is canceled and restarted then there is
    // a delay of 500 milliseconds (to ensure that user has finished typing or not) before validation
    // actually occurs and UI state is updated.
    private var validationJob: Job? = null

    private val delayTime = 500L

    fun onEmailTextChanged(emailText: String){
        _reauthenticateEmailUiState.update {
                reauthenticateEmailUiState ->
            reauthenticateEmailUiState.copy(email = emailText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _reauthenticateEmailUiState.update {
                    reauthenticateEmailUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                reauthenticateEmailUiState.copy(emailError = emailValidation.errorMessage)
            }
        }
    }

    fun onPasswordTextChanged(passwordText: String){
        _reauthenticateEmailUiState.update {
                reauthenticateEmailUiState ->
            reauthenticateEmailUiState.copy(password = passwordText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _reauthenticateEmailUiState.update {
                    reauthenticateEmailUiState ->
                val passwordValidation = validatePasswordUseCase(password = passwordText,
                    shouldValidatePasswordPattern = false)
                reauthenticateEmailUiState.copy(
                    password = passwordText,
                    passwordError = passwordValidation.errorMessage
                )
            }
        }
    }

    fun reauthenticate(){

    }
}