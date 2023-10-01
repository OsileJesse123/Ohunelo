package com.jesse.ohunelo.presentation.uistates

data class OnboardingUiState(
    val shouldKeepSplashScreenOn: Boolean = true,
    /** first is a boolean determining whether or not to navigate to the next screen.
     *
     *  second is a string which determines what screen to navigate to.
     * **/
    val navigateToNextScreen: Pair<Boolean, String> = Pair(false, "")
)
