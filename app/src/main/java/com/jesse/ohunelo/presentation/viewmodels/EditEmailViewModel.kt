package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.presentation.uistates.EditEmailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEmailViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _editEmailUiState: MutableStateFlow<EditEmailUiState> = MutableStateFlow(
        EditEmailUiState()
    )
    val editEmailUiState get() = _editEmailUiState.asStateFlow()

    private var validationJob: Job? = null
    private val delayTime = 500L

    fun onEmailTextChanged(emailText: String){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(email = emailText)
        }
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _editEmailUiState.update {
                    editEmailUiState ->
                val emailValidation = validateEmailUseCase(emailText)
                editEmailUiState.copy(emailError = emailValidation.errorMessage, isEnabled = emailValidation.successful)
            }
        }
    }

    fun editEmail(){
        viewModelScope.launch {
            _editEmailUiState.update {
                    editEmailUiState ->
                editEmailUiState.copy(
                    isLoading = true
                )
            }
            if(_editEmailUiState.value.isEmailValid()){
                val result = authenticationRepository.updateUserEmail(_editEmailUiState.value.email)
                when(result){

                }
            }
        }
    }
}