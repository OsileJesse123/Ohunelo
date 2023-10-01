package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateNameUseCase
import com.jesse.ohunelo.presentation.uistates.UpdateUsernameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UpdateUsernameViewModel @Inject constructor(
    private val validateNameUseCase: ValidateNameUseCase,
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _updateUsernameUiStateFlow: MutableStateFlow<UpdateUsernameUiState> =
        MutableStateFlow(UpdateUsernameUiState())
    val updateUsernameUiStateFlow get() = _updateUsernameUiStateFlow.asStateFlow()

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
            _updateUsernameUiStateFlow.update {
                    updateUsernameUiState ->
                val userNameValidation = validateNameUseCase(usernameText)
                updateUsernameUiState.copy(
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
            _updateUsernameUiStateFlow.update {
                    updateUsernameUiState ->
                val userNameValidation = validateNameUseCase(usernameText)
                updateUsernameUiState.copy(
                    lastName = usernameText,
                    lastNameError = userNameValidation.errorMessage
                )
            }
        }
    }

    fun updateUserName(){
        viewModelScope.launch {
            if(_updateUsernameUiStateFlow.value.isFormValid()){
                _updateUsernameUiStateFlow.update {
                        updateUsernameUiState ->
                    updateUsernameUiState.copy(
                        isEnabled = false
                    )
                }
                // Ensure that first and last name have first letter as capital letter and remaining letters
                // as small letters
                val firstName = _updateUsernameUiStateFlow.value.firstName.lowercase(Locale.ROOT).replaceFirstChar {
                    // This converts the first letter to capital letter
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else {
                        it.toString()
                    }
                }
                val lastName = _updateUsernameUiStateFlow.value.lastName.lowercase(Locale.ROOT).replaceFirstChar {
                    // This converts the first letter to capital letter
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else {
                        it.toString()
                    }
                }

                when(val updateUserNameResult = authenticationRepository.updateTheUserName(firstName, lastName)){
                    is OhuneloResult.Success -> {
                        _updateUsernameUiStateFlow.update {
                                updateUsernameUiState ->
                            updateUsernameUiState.copy(
                                navigateToNextScreen = true,
                                isEnabled = true
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        _updateUsernameUiStateFlow.update {
                                updateUsernameUiState ->
                            updateUsernameUiState.copy(
                                showErrorMessage = Pair(true, updateUserNameResult.errorMessage),
                                isEnabled = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun onErrorMessageShown(){
        _updateUsernameUiStateFlow.update {
                updateUsernameUiState ->
            updateUsernameUiState.copy(
                showErrorMessage = Pair(false, null)
            )
        }
    }

    fun onNavigationToNextScreen(){
        _updateUsernameUiStateFlow.update {
                updateUsernameUiState ->
            updateUsernameUiState.copy(
                navigateToNextScreen = false,
            )
        }
    }
}