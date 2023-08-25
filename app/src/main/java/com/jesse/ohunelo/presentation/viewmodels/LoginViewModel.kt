package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
): ViewModel() {

    private val _loginUiStateFlow: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiStateFlow get() = _loginUiStateFlow.asStateFlow()

    fun onEmailTextChanged(emailText: String){
        _loginUiStateFlow.update {
            loginUiState ->
            val emailValidation = validateEmailUseCase(emailText)
            loginUiState.copy(
                email = emailText,
                emailError = emailValidation.errorMessage
            )
        }
    }

    fun onPasswordTextChanged(passwordText: String){
        _loginUiStateFlow.update {
            loginUiState ->
            val passwordValidation = validatePasswordUseCase(passwordText)
            loginUiState.copy(
                password = passwordText,
                passwordError = passwordValidation.errorMessage
            )
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