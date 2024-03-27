package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.presentation.uistates.VerifyEmailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _verifyEmailUiState: MutableStateFlow<VerifyEmailUiState> =
        MutableStateFlow(VerifyEmailUiState())
    val verifyEmailUiState get() = _verifyEmailUiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.user.collectLatest {
                authUser ->
                _verifyEmailUiState.update {
                    verifyEmailUiState ->
                    verifyEmailUiState.copy(
                        userEmail = authUser?.email ?: " "
                    )
                }
            }
        }
        verifyUserEmail()
        hasTheUserBeenVerified()
    }

    private fun verifyUserEmail(){
        viewModelScope.launch {
            authenticationRepository.verifyUserEmail()
        }
    }

    fun resendEmailLink(){
        viewModelScope.launch {
            _verifyEmailUiState.update {
                verifyEmailUiState ->
                verifyEmailUiState.copy(isEnabled = false)
            }
            when(val result = authenticationRepository.verifyUserEmail()){
                is OhuneloResult.Success -> {
                    _verifyEmailUiState.update {
                            verifyEmailUiState ->
                        verifyEmailUiState.copy(isEnabled = true)
                    }
                }
                is OhuneloResult.Error ->{
                    _verifyEmailUiState.update {
                            verifyEmailUiState ->
                        verifyEmailUiState.copy(
                            isEnabled = true,
                            showErrorMessage = Pair(true, result.errorMessage)
                        )
                    }
                }
            }
        }
    }

    fun onErrorMessageShown(){
        _verifyEmailUiState.update {
                verifyEmailUiState ->
            verifyEmailUiState.copy(
                showErrorMessage = Pair(false, null)
            )
        }
    }

    private fun hasTheUserBeenVerified(){
        // This runs a check every 1.5 second to see if user has verified their mail and updates
        // the UI as per required.
        viewModelScope.launch {
            while (isActive){
                delay(1500L)
                val userEmailVerified = authenticationRepository.hasTheUserBeenVerified()
                if (userEmailVerified){
                    _verifyEmailUiState.update {
                            verifyEmailUiState ->
                        verifyEmailUiState.copy(
                            navigateToNextScreen = true
                        )
                    }
                    cancel()
                }

            }
        }
    }
}