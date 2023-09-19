package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.presentation.uistates.ResetPasswordUiState
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

    fun onEmailTextChanged(emailText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(500L)
            _resetPasswordUiStateFlow.update {
                    resetPasswordUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                resetPasswordUiState.copy(
                    email = emailText,
                    emailError = emailValidation.errorMessage
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
            if(_resetPasswordUiStateFlow.value.isFormValid()){
                _resetPasswordUiStateFlow.update {
                        resetPasswordUiState ->
                    resetPasswordUiState.copy(
                        isEnabled = false
                    )
                }
                val result = authenticationRepository.sendPasswordResetEmail(email =
                _resetPasswordUiStateFlow.value.email)
                when(result){
                    is OhuneloResult.Success -> {
                        _resetPasswordUiStateFlow.update {
                                resetPasswordUiState ->
                            resetPasswordUiState.copy(
                                isEnabled = true,
                                showConfirmationMessage = Pair(true, result.data)
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        _resetPasswordUiStateFlow.update {
                                resetPasswordUiState ->
                            resetPasswordUiState.copy(
                                isEnabled = true,
                                showConfirmationMessage = Pair(true, result.errorMessage)
                            )
                        }
                    }
                }
            }
        }
    }
}