package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.presentation.uistates.VerifyEmailUiState
import com.jesse.ohunelo.util.AuthenticationException
import com.jesse.ohunelo.util.NetworkErrorException
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val uiActionFlow: MutableSharedFlow<UiAction> = MutableSharedFlow(replay = 1)
    val userEmail: StateFlow<String> = authenticationRepository.user.flatMapLatest {
        user ->
        flow {
            user?.let {
                val (_, _, email, _) = it
                emit(email ?: "")
            } ?: emit("")
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ""
    )

    val verifyEmailUiState: StateFlow<VerifyEmailUiState> = uiActionFlow.flatMapLatest{
        uiAction ->
        flow {
            when(uiAction){
                UiAction.Send -> {
                    emit(VerifyEmailUiState(isEnabled = false))
                    when(val result = authenticationRepository.verifyUserEmail()){
                        is OhuneloResult.Success -> {
                            emit(VerifyEmailUiState(isEnabled = true))
                        }
                        is OhuneloResult.Error ->{
                            val errorMessage = when(result.error){
                                is NetworkErrorException -> UiText.StringResource(R.string.network_error_occured)
                                is AuthenticationException.TooManyRequestsException -> UiText.StringResource(R.string.too_many_requests)
                                is Exception ->  UiText.StringResource(R.string.send_email_link_failed)
                                else -> null
                            }
                            emit(VerifyEmailUiState(
                                isEnabled = true,
                                showErrorMessage = Pair(true, errorMessage)
                            ))
                        }
                    }
                }
                UiAction.OnErrorMessageShown -> {
                    emit(VerifyEmailUiState(showErrorMessage = Pair(false, null)))
                }
                UiAction.NavigateToNextScreen -> {
                    emit(VerifyEmailUiState(isEnabled = false, navigateToNextScreen = true))
                }
            }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = VerifyEmailUiState()
    )

    init {
        sendUiAction(UiAction.Send)
        hasTheUserBeenVerified()
    }

    fun sendUiAction(uiAction: UiAction) {
        viewModelScope.launch {
            uiActionFlow.emit(uiAction)
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
                    uiActionFlow.emit(UiAction.NavigateToNextScreen)
                    cancel()
                }

            }
        }
    }
}

enum class UiAction{
    Send,
    OnErrorMessageShown,
    NavigateToNextScreen
}