package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
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
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val authenticationRepository: AuthenticationRepository
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
        viewModelScope.launch {
            if(_registerUiStateFlow.value.isFormValid()){
                _registerUiStateFlow.update {
                    registerUiState ->
                    registerUiState.copy(
                        isEnabled = false
                        )
                }
                val registerResult = authenticationRepository.registerUserWithEmailAndPassword(
                    // Ensure that first and last name have first letter as capital letter and remaining letters
                    // as small letters
                    firstName = _registerUiStateFlow.value.firstName.lowercase(Locale.ROOT).replaceFirstChar {
                        // This converts the first letter to capital letter
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale.getDefault()
                            )
                        } else {
                            it.toString()
                        }
                    },
                    lastName = _registerUiStateFlow.value.lastName.lowercase(Locale.ROOT).replaceFirstChar {
                        // This converts the first letter to capital letter
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale.getDefault()
                            )
                        } else {
                            it.toString()
                        }
                    },
                    email = _registerUiStateFlow.value.email,
                    password = _registerUiStateFlow.value.password)

                when(registerResult){
                    is OhuneloResult.Success -> {
                        Timber.e("ViewModel Registration Successful, user: (${registerResult.data})")
                        _registerUiStateFlow.update {
                                registerUiState ->
                            registerUiState.copy(
                                navigateToNextScreen = true,
                                isEnabled = true
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        _registerUiStateFlow.update {
                                registerUiState ->
                            registerUiState.copy(
                                showErrorMessage = Pair(true, registerResult.errorMessage),
                                isEnabled = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun onErrorMessageShown(){
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(
                showErrorMessage = Pair(false, null)
            )
        }
    }

    fun onNavigationToNextScreen(){
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(
                navigateToNextScreen = false,
            )
        }
    }

}