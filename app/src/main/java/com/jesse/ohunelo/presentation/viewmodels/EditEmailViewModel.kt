package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateEmailUseCase
import com.jesse.ohunelo.presentation.uistates.EditEmailUiState
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UpdateStatus
import com.jesse.ohunelo.util.UserType
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
                editEmailUiState.copy(emailError = emailValidation.errorMessage)
            }
        }
    }

    fun editEmail(){
            if(_editEmailUiState.value.isEmailValid()){
                viewModelScope.launch {
                    _editEmailUiState.update {
                            editEmailUiState ->
                        editEmailUiState.copy(
                            isLoading = true,
                        )
                    }
                when(val result = authenticationRepository.updateUserEmail(_editEmailUiState.value.email)){
                    is OhuneloResult.Success ->{
                        if (result.data == UpdateStatus.SUCCESS){
                          _editEmailUiState.update {
                                  editEmailUiState ->
                              editEmailUiState.copy(
                                  message = UiText.StringResource(R.string.edit_was_successful),
                                  isLoading = false
                              )
                          }
                          return@launch
                        }
                    }
                    is OhuneloResult.Error -> {
                        if (result.data == UpdateStatus.REAUTHENTICATE){
                            _editEmailUiState.update {
                                    editEmailUiState ->
                                editEmailUiState.copy(
                                    message = result.errorMessage,
                                    reauthenticate = Pair(true, UserType.getUserType(authenticationRepository.getUserType()))
                                )
                            }
                            return@launch
                        }
                        _editEmailUiState.update {
                                editEmailUiState ->
                            editEmailUiState.copy(
                                message = result.errorMessage,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun renableButton(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                isLoading = false
            )
        }
    }

    fun onMessageShown(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                message = null
            )
        }
    }

    fun onReauthenticateInitiated(){
        _editEmailUiState.update {
                editEmailUiState ->
            editEmailUiState.copy(
                reauthenticate = Pair(false, null)
            )
        }
    }
}