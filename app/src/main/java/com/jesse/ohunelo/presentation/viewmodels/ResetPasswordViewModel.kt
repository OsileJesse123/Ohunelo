package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.domain.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.presentation.uistates.ResetPasswordUiState
import com.jesse.ohunelo.util.AuthenticationException
import com.jesse.ohunelo.util.NetworkErrorException
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
class ResetPasswordViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _resetPasswordUiStateFlow: MutableStateFlow<ResetPasswordUiState> = MutableStateFlow(ResetPasswordUiState())
    val resetPasswordUiStateFlow get() = _resetPasswordUiStateFlow.asStateFlow()

    private var validationJob: Job? = null
    private val delayTime = 500L

    fun onEmailTextChanged(emailText: String){
        _resetPasswordUiStateFlow.update {
                resetPasswordUiState ->
            resetPasswordUiState.copy(email = emailText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _resetPasswordUiStateFlow.update {
                    resetPasswordUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                resetPasswordUiState.copy(
                    emailError = emailValidation.errorMessage,
                    isEnabled = resetPasswordUiState.email.isNotEmpty()
                )
            }
        }
    }

    fun onMessageShown(){
        _resetPasswordUiStateFlow.update {
                resetPasswordUiState ->
            resetPasswordUiState.copy(
                showConfirmationMessage = Pair(false, null),
                showErrorMessage = Pair(false, null)
            )
        }
    }

    fun submit(){
        viewModelScope.launch {
            // Disable all views
            _resetPasswordUiStateFlow.update {
                    resetPasswordUiState ->
                resetPasswordUiState.copy(
                    isEnabled = false,
                    isLoading = true
                )
            }
            // A short delay to ensure that state is up to date before validating
            delay(delayTime)
            if(_resetPasswordUiStateFlow.value.isFormValid()){
                val result = authenticationRepository.sendPasswordResetEmail(email =
                _resetPasswordUiStateFlow.value.email)
                when(result){
                    is OhuneloResult.Success -> {
                        _resetPasswordUiStateFlow.update {
                                resetPasswordUiState ->
                            resetPasswordUiState.copy(
                                isEnabled = true,
                                isLoading = false,
                                showConfirmationMessage = Pair(true, UiText.StringResource(R.string.reset_password_email))
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        val errorMessage = when(result.error){
                            is AuthenticationException.NoUserException -> UiText.StringResource(R.string.reset_password_email_failed)
                            is AuthenticationException.UserDisabledException -> UiText.StringResource(resId = R.string.user_disabled)
                            is AuthenticationException.UserTokenExpiredException -> UiText.StringResource(resId = R.string.user_token_expired)
                            is AuthenticationException.InvalidUserTokenException -> UiText.StringResource(resId = R.string.invalid_user_token)
                            is NetworkErrorException -> UiText.StringResource(resId = R.string.network_error_occured)
                            is Exception -> UiText.StringResource(resId = R.string.reset_password_email_failed)
                            else -> null
                        }
                        _resetPasswordUiStateFlow.update {
                                resetPasswordUiState ->
                            resetPasswordUiState.copy(
                                isEnabled = true,
                                isLoading = false,
                                showConfirmationMessage = Pair(true, errorMessage)
                            )
                        }
                    }
                }
            } else {
                _resetPasswordUiStateFlow.update {
                        resetPasswordUiState ->
                    resetPasswordUiState.copy(
                        isEnabled = true,
                        isLoading = false,
                    )
                }
            }
        }
    }
}