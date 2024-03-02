package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.domain.usecase.ValidateNameUseCase
import com.jesse.ohunelo.presentation.uistates.UpdateProfileUiState
import com.jesse.ohunelo.util.FIRST_NAME_MAX_LENGTH
import com.jesse.ohunelo.util.LAST_NAME_MAX_LENGTH
import com.jesse.ohunelo.util.SPLIT_FIRST_AND_LAST_NAME_WITH_WHITESPACE
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val validateNameUseCase: ValidateNameUseCase,
    private val authenticationRepository: AuthenticationRepository,
): ViewModel() {

    private val _updateProfileUiState = MutableStateFlow(UpdateProfileUiState())
    val updateProfileUiState = _updateProfileUiState.asStateFlow()

    // This is to ensure that validation of text in EditText is optimal. When user enters text,
    // validation doesn't happen immediately, initial task is canceled and restarted then there is
    // a delay of 500 milliseconds (to ensure that user has finished typing or not) before validation
    // actually occurs and UI state is updated.
    private var validationJob: Job? = null

    private val delayTime = 500L

    init {
        viewModelScope.launch {
            authenticationRepository.user.collectLatest {
                it?.let {
                    user ->
                    _updateProfileUiState.update {
                            updateProfileUiState ->
                        val (_, _, _, username) = user
                        updateProfileUiState.copy(
                            firstName = username?.split(SPLIT_FIRST_AND_LAST_NAME_WITH_WHITESPACE)?.get(0) ?: "",
                            lastName = username?.split(SPLIT_FIRST_AND_LAST_NAME_WITH_WHITESPACE)?.get(1) ?: "",
                        )
                    }
                }

            }

        }
    }

    fun updateProfile(){
        viewModelScope.launch {
            if(_updateProfileUiState.value.isFormValid()){
                _updateProfileUiState.update {
                    updateProfileUiState ->
                    updateProfileUiState.copy(
                        isEnabled = false
                    )
                }
                // Ensure that first and last name have first letter as capital letter and remaining letters
                // as small letters
                val firstName = _updateProfileUiState.value.firstName.lowercase(Locale.ROOT).replaceFirstChar {
                    // This converts the first letter to capital letter
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else {
                        it.toString()
                    }
                }
                val lastName = _updateProfileUiState.value.lastName.lowercase(Locale.ROOT).replaceFirstChar {
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
                        _updateProfileUiState.update {
                                updateProfileUiState ->
                            authenticationRepository.updateUser()
                            updateProfileUiState.copy(
                                exitUpdateProfile = true,
                                isEnabled = true,
                                showSuccessMessage = Pair(true, UiText.StringResource(R.string.edit_was_successful))
                            )
                        }
                    }
                    is OhuneloResult.Error -> {
                        _updateProfileUiState.update {
                                updateProfileUiState ->
                            updateProfileUiState.copy(
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
        _updateProfileUiState.update {
                updateProfileUiState ->
            updateProfileUiState.copy(
                showErrorMessage = Pair(false, null)
            )
        }
    }

    fun onSuccessMessageShown(){
        _updateProfileUiState.update {
                updateProfileUiState ->
            updateProfileUiState.copy(
                showSuccessMessage = Pair(false, null)
            )
        }
    }

    fun onFirstNameTextChanged(firstNameText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _updateProfileUiState.update {
                    updateProfileUiState ->
                val userNameValidation = validateNameUseCase(firstNameText, FIRST_NAME_MAX_LENGTH)
                updateProfileUiState.copy(
                    firstName = firstNameText,
                    firstNameError = userNameValidation.errorMessage
                )
            }
        }
    }

    fun onLastNameTextChanged(lastNameText: String){
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            delay(delayTime)
            _updateProfileUiState.update {
                    updateProfileUiState ->
                val userNameValidation = validateNameUseCase(lastNameText, LAST_NAME_MAX_LENGTH)
                updateProfileUiState.copy(
                    lastName = lastNameText,
                    lastNameError = userNameValidation.errorMessage
                )
            }
        }
    }
}