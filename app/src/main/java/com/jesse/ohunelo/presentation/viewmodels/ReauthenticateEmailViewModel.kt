package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.ReauthenticateEmailUiState
import com.jesse.ohunelo.util.AuthenticationException
import com.jesse.ohunelo.util.UiText
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
    private val authenticationRepository: AuthenticationRepository
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
                reauthenticateEmailUiState.copy(
                    emailError = emailValidation.errorMessage,
                    isEnabled = reauthenticateEmailUiState.email.isNotEmpty() && reauthenticateEmailUiState.password.isNotEmpty()
                )
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
                    passwordError = passwordValidation.errorMessage,
                    isEnabled = reauthenticateEmailUiState.email.isNotEmpty() && reauthenticateEmailUiState.password.isNotEmpty()
                )
            }
        }
    }

    fun reauthenticate(){
        viewModelScope.launch {
            delay(delayTime)
            if (_reauthenticateEmailUiState.value.isFormValid()){
                // Disable buttons and show loader in UI
                _reauthenticateEmailUiState.update {
                        reauthenticateEmailUiState ->
                    reauthenticateEmailUiState.copy(
                        isEnabled = false,
                        isLoading = true
                    )
                }
                val reauthenticateResult = authenticationRepository
                    .reauthenticateUserEmailPassword(
                        email = _reauthenticateEmailUiState.value.email,
                        password = _reauthenticateEmailUiState.value.password
                )
                when(reauthenticateResult){
                    is OhuneloResult.Success -> {
                        _reauthenticateEmailUiState.update {
                                reauthenticateEmailUiState ->
                            reauthenticateEmailUiState.copy(
                                dismiss = true,
                                message = UiText.StringResource(R.string.reauthenticate_success)
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        when(reauthenticateResult.error){
                            is AuthenticationException.NoUserException -> {
                                _reauthenticateEmailUiState.update {
                                        reauthenticateEmailUiState ->
                                    reauthenticateEmailUiState.copy(
                                        message = UiText.StringResource(R.string.reauthenticate_fail),
                                        logout = true
                                    )
                                }
                            }
                            is AuthenticationException.InvalidCredentialsException -> {
                                _reauthenticateEmailUiState.update {
                                        reauthenticateEmailUiState ->
                                    reauthenticateEmailUiState.copy(
                                        message = UiText.StringResource(R.string.invalid_credentials),
                                        isEnabled = true,
                                        isLoading = false
                                    )
                                }
                            }
                            is AuthenticationException.InvalidUserException -> {
                                _reauthenticateEmailUiState.update {
                                        reauthenticateEmailUiState ->
                                    reauthenticateEmailUiState.copy(
                                        message = UiText.StringResource(R.string.no_user_record_corresponding),
                                        logout = true
                                    )
                                }
                            }
                            else -> {
                                _reauthenticateEmailUiState.update {
                                        reauthenticateEmailUiState ->
                                    reauthenticateEmailUiState.copy(
                                        message = UiText.StringResource(R.string.reauthenticate_fail),
                                        isEnabled = true,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                _reauthenticateEmailUiState.update {
                        reauthenticateEmailUiState ->
                    reauthenticateEmailUiState.copy(
                        isEnabled = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onMessageShown(){
        _reauthenticateEmailUiState.update {
                reauthenticateEmailUiState ->
            reauthenticateEmailUiState.copy(
                message = null
            )
        }
    }

    fun onLogout(){
        viewModelScope.launch {
            authenticationRepository.logout()
            _reauthenticateEmailUiState.update {
                    reauthenticateEmailUiState ->
                reauthenticateEmailUiState.copy(
                    logout = false
                )
            }
        }
    }
}