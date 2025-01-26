package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.domain.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.domain.usecase.ValidateNameUseCase
import com.jesse.ohunelo.presentation.uistates.RegisterUiState
import com.jesse.ohunelo.util.AuthenticationException
import com.jesse.ohunelo.util.FIRST_NAME_MAX_LENGTH
import com.jesse.ohunelo.util.LAST_NAME_MAX_LENGTH
import com.jesse.ohunelo.util.NetworkErrorException
import com.jesse.ohunelo.util.UiText
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
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(firstName = usernameText,)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val userNameValidation = validateNameUseCase(usernameText, FIRST_NAME_MAX_LENGTH)
                registerUiState.copy(
                    firstNameError = userNameValidation.errorMessage,
                    isEnabled = areInputFieldsFilled()
                )
            }
        }
    }

    fun onLastNameTextChanged(usernameText: String){
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(lastName = usernameText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val userNameValidation = validateNameUseCase(usernameText, LAST_NAME_MAX_LENGTH)
                registerUiState.copy(
                    lastNameError = userNameValidation.errorMessage,
                    isEnabled = areInputFieldsFilled()
                )
            }
        }
    }

    fun onEmailTextChanged(emailText: String){
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(email = emailText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                registerUiState.copy(
                    emailError = emailValidation.errorMessage,
                    isEnabled = areInputFieldsFilled()
                )
            }
        }
    }

    fun onPasswordTextChanged(passwordText: String){
        _registerUiStateFlow.update {
                registerUiState ->
            registerUiState.copy(password = passwordText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _registerUiStateFlow.update {
                    registerUiState ->
                val passwordValidation = validatePasswordUseCase(password = passwordText,
                    shouldValidatePasswordPattern = true)
                registerUiState.copy(
                    passwordError = passwordValidation.errorMessage,
                    isEnabled = areInputFieldsFilled()
                )
            }
        }
    }

    fun register(){
        viewModelScope.launch {
            // Disable all views
            _registerUiStateFlow.update {
                    registerUiState ->
                registerUiState.copy(
                    isEnabled = false,
                    isLoading = true
                )
            }
            // A short delay to ensure that state is up to date before validating
            delay(delayTime)
            if(_registerUiStateFlow.value.isFormValid()){
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
                                isEnabled = true,
                                isLoading = false
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        val errorMessage = when(registerResult.error){
                            is AuthenticationException.NoUserException -> UiText.StringResource(resId = R.string.user_registered_but_user_null)
                            is AuthenticationException.EmailAlreadyInUseException -> UiText.StringResource(resId = R.string.email_already_in_use)
                            is AuthenticationException.AccountExistWithDifferentCredentialException -> UiText.StringResource(resId = R.string.account_already_exist)
                            is AuthenticationException.CredentialAlreadyInUseException -> UiText.StringResource(resId = R.string.credential_already_in_use)
                            is NetworkErrorException -> UiText.StringResource(resId = R.string.network_error_occured)
                            is Exception -> UiText.StringResource(resId = R.string.registration_failed)
                            else -> null
                        }
                        _registerUiStateFlow.update {
                                registerUiState ->
                            registerUiState.copy(
                                showErrorMessage = Pair(true, errorMessage),
                                isEnabled = true,
                                isLoading = false
                            )
                        }
                    }
                }
            } else{
                _registerUiStateFlow.update {
                        registerUiState ->
                    registerUiState.copy(
                        isEnabled = true,
                        isLoading = false
                    )
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

    private fun areInputFieldsFilled(): Boolean{
        return _registerUiStateFlow.value.firstName.isNotEmpty() &&
                _registerUiStateFlow.value.lastName.isNotEmpty() &&
                _registerUiStateFlow.value.email.isNotEmpty() &&
                _registerUiStateFlow.value.password.isNotEmpty()
    }

}