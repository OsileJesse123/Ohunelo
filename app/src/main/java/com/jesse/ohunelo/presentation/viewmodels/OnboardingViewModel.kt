package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.presentation.uistates.OnboardingUiState
import com.jesse.ohunelo.util.HOME_FRAGMENT
import com.jesse.ohunelo.util.LOGIN_FRAGMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _onboardingUiStateFlow: MutableStateFlow<OnboardingUiState> = MutableStateFlow(OnboardingUiState())
    val onboardingUiStateFlow get() = _onboardingUiStateFlow.asStateFlow()

    init {
        updateOnboardingUiState()
    }

    private fun updateOnboardingUiState(){
        viewModelScope.launch {
            if (authenticationRepository.isAFirstTimeUser()){
                // If the user is a first time user then just stop showing the splash screen
                _onboardingUiStateFlow.update {
                    onboardingUiState ->
                    onboardingUiState.copy(
                        shouldKeepSplashScreenOn = false
                    )
                }
            } else{
                // If the user is not a first time user
                if (authenticationRepository.isUserLoggedIn()){
                    // If the user is logged in
                    _onboardingUiStateFlow.update {
                            onboardingUiState ->
                        onboardingUiState.copy(
                            navigateToNextScreen = Pair(true, HOME_FRAGMENT),
                            shouldKeepSplashScreenOn = false
                        )
                    }
                } else {
                    // If the user is not logged in
                    _onboardingUiStateFlow.update {
                            onboardingUiState ->
                        onboardingUiState.copy(
                            navigateToNextScreen = Pair(true, LOGIN_FRAGMENT),
                            shouldKeepSplashScreenOn = false
                        )
                    }
                }
            }
        }
    }

    fun startCooking(){
        viewModelScope.launch {
            authenticationRepository.updateIsAFirstTimeUser()
            _onboardingUiStateFlow.update {
                onboardingUiState ->
                onboardingUiState.copy(
                    navigateToNextScreen = Pair(true, LOGIN_FRAGMENT)
                )
            }
        }
    }
}