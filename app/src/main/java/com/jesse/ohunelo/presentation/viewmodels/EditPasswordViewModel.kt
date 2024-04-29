package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidatePasswordUseCase
import com.jesse.ohunelo.presentation.uistates.EditPasswordUiState
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
                editPasswordUiState.copy(passwordError = passwordValidation.errorMessage)
            }
        }
    }

    fun startEditPassword(){
        if(_editPasswordUiState.value.isPasswordValid()){
            _editPasswordUiState.update {
                editPasswordUiState ->
                editPasswordUiState.copy(
                    isLoading = true,
                    reauthenticate = true
                )
            }
        }
    }

    fun onMessageShown(){
        _editPasswordUiState.update {
            editPasswordUiState ->
            editPasswordUiState.copy(
                message = null
            )
        }
    }

    fun onNavigateBack(){
        _editPasswordUiState.update {
            editPasswordUiState ->
            editPasswordUiState.copy(
                navigateBack = false
            )
        }
    }

    fun finishEditPassword(){
        if (_editPasswordUiState.value.isPasswordValid()){
            viewModelScope.launch {
                _editPasswordUiState.update {
                        editPasswordUiState ->
                    editPasswordUiState.copy(
                        isLoading = true
                    )
                }
                when(val result = authenticationRepository.updateUserPassword(_editPasswordUiState.value.password)){
                    is OhuneloResult.Success -> {
                        _editPasswordUiState.update {
                                editPasswordUiState ->
                            editPasswordUiState.copy(
                                message = result.data
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        _editPasswordUiState.update {
                                editPasswordUiState ->
                            editPasswordUiState.copy(
                                isLoading = false,
                                message = result.errorMessage
                            )
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
                isLoading = false
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