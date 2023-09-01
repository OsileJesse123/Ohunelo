package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
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
        if(_loginUiStateFlow.value.isFormValid()){
           _loginUiStateFlow.update {
               loginUiState ->
               loginUiState.copy(
                   navigateToNextScreen = true
               )
           }
        }
    }
}