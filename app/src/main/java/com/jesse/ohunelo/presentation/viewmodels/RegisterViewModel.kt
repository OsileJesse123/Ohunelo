package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.domain.usecase.ValidateNameUseCase
import com.jesse.ohunelo.presentation.uistates.RegisterUiState
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
class RegisterViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateNameUseCase: ValidateNameUseCase
): ViewModel() {

    private val _registerUiStateFlow: MutableStateFlow<RegisterUiState> =
        MutableStateFlow(RegisterUiState())
    val registerUiStateFlow: StateFlow<RegisterUiState> get() = _registerUiStateFlow.asStateFlow()

    // This is to ensure that validation of text in EditText is optimal. When user enters text,
    // validation doesn't happen immediately, initial task is canceled and restarted then there is
    // a delay of 500 milliseconds (to ensure that user has finished typing or not) before validation
    // actually occurs and UI state is updated.
    private var validationJob: Job? = null

    private val delayTime = 500L

    fun onFirstNameTextChanged(usernameText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val userNameValidation = validateNameUseCase(usernameText)
                registerUiState.copy(
                    firstName = usernameText,
                    firstNameError = userNameValidation.errorMessage
                )
            }
        }
    }

    fun onLastNameTextChanged(usernameText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val userNameValidation = validateNameUseCase(usernameText)
                registerUiState.copy(
                    lastName = usernameText,
                    lastNameError = userNameValidation.errorMessage
                )
            }
        }
    }

    fun onEmailTextChanged(emailText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                registerUiState.copy(
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
            _registerUiStateFlow.update {
                    registerUiState ->
                val passwordValidation = validatePasswordUseCase(password = passwordText,
                    shouldValidatePasswordPattern = true)
                registerUiState.copy(
                    password = passwordText,
                    passwordError = passwordValidation.errorMessage
                )
            }
        }
    }

    fun register(){
        if(_registerUiStateFlow.value.isFormValid()){
            _registerUiStateFlow.update {
                    registerUiState ->
                registerUiState.copy(
                    navigateToNextScreen = true
                )
            }
        }
    }

    fun onNavigationToNextScreen(){
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(
                navigateToNextScreen = false
            )
        }
    }

}