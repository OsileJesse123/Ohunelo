package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.domain.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.EditPasswordUiState
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
class EditPasswordViewModel @Inject constructor(
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {
    private val _editPasswordUiState: MutableStateFlow<EditPasswordUiState> =
        MutableStateFlow(EditPasswordUiState())
    val editPasswordUiState get() = _editPasswordUiState.asStateFlow()

    private var validationJob: Job? = null
    private val delayTime = 500L

    fun onPasswordTextChanged(passwordText: String){
        _editPasswordUiState.update {
            editPasswordUiState ->
            editPasswordUiState.copy(password = passwordText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _editPasswordUiState.update {
                editPasswordUiState ->
                val passwordValidation = validatePasswordUseCase(passwordText, true)
                editPasswordUiState.copy(
                    passwordError = passwordValidation.errorMessage,
                    isEnabled = editPasswordUiState.password.isNotEmpty()
               )
            }
        }
    }

    fun onMessageShown(){
        _editPasswordUiState.update {
            editPasswordUiState ->
            editPasswordUiState.copy(
                message = null,
                logout = false
            )
        }
    }

    fun onLogout(){
        viewModelScope.launch {
            authenticationRepository.logout()
            _editPasswordUiState.update {
                    editPasswordUiState ->
                editPasswordUiState.copy(
                    logout = false
                )
            }
        }
    }

    fun editPassword(){
        if (_editPasswordUiState.value.isPasswordValid()){
            viewModelScope.launch {
                _editPasswordUiState.update {
                        editPasswordUiState ->
                    editPasswordUiState.copy(
                        isLoading = true,
                        isEnabled = false
                    )
                }
                when(val result = authenticationRepository.updateUserPassword(_editPasswordUiState.value.password)){
                    is OhuneloResult.Success -> {
                        _editPasswordUiState.update {
                                editPasswordUiState ->
                            editPasswordUiState.copy(
                                message = UiText.StringResource(R.string.edit_was_successful),
                                logout = true
                            )
                        }

                    }
                    is OhuneloResult.Error -> {
                        when(result.error){
                            // This means the password is weak
                            is AuthenticationException.WeakPasswordException -> {
                                _editPasswordUiState.update {
                                        editPasswordUiState ->
                                    editPasswordUiState.copy(
                                        isLoading = false,
                                        message = UiText.StringResource(R.string.weak_password),
                                        logout = false,
                                        isEnabled = true
                                    )
                                }
                            }
                            // This means user hasn't logged in for a while and needs to be reauthenticated
                            is AuthenticationException.AuthRecentLoginRequiredException -> {
                                _editPasswordUiState.update {
                                        editPasswordUiState ->
                                    editPasswordUiState.copy(
                                        message = UiText.StringResource(R.string.reauthenticate_message),
                                        reauthenticate = true
                                    )
                                }
                            }
                            // // These errors means the user has to be logged out
                            is AuthenticationException.UserTokenExpiredException,
                            is AuthenticationException.UserDisabledException,
                            is AuthenticationException.NoUserException,
                            is AuthenticationException.InvalidUserTokenException -> {
                                _editPasswordUiState.update {
                                        editPasswordUiState ->
                                    editPasswordUiState.copy(
                                        message = UiText.StringResource(R.string.user_credential_no_longer_valid),
                                        logout = true
                                    )
                                }
                            }
                            else -> {
                                _editPasswordUiState.update {
                                        editPasswordUiState ->
                                    editPasswordUiState.copy(
                                        isLoading = false,
                                        message = UiText.StringResource(R.string.edit_password_failed),
                                        logout = false,
                                        isEnabled = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun renableButton(){
        _editPasswordUiState.update {
                editPasswordUiState ->
            editPasswordUiState.copy(
                isLoading = false,
                isEnabled = true
            )
        }
    }

    fun onReauthenticateInitiated(){
        _editPasswordUiState.update {
                editPasswordUiState ->
            editPasswordUiState.copy(
                reauthenticate = false
            )
        }
    }
}