package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Nutrition
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.*
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.data.repository.RecipeRepository
import com.jesse.ohunelo.domain.usecase.FormatHomeScreenDataUseCase
import com.jesse.ohunelo.presentation.uistates.HomeUiState
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val formatHomeScreenDataUseCase: FormatHomeScreenDataUseCase
): ViewModel() {

    private val _homeUiStateFlow: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiStateFlow: StateFlow<HomeUiState> get() = _homeUiStateFlow.asStateFlow()

    private var getRecipesByMealTypeJob: Job? = null

    var selectedRecipeCategory = "Main Course"
        private set

    init {
        updateGreeting()
        getUserName()
        getRecipesForHomeScreen()
    }

    fun updateUser(){
        viewModelScope.launch {
            authenticationRepository.updateUser()
        }
    }

    private fun getUserName(){
        viewModelScope.launch {

            authenticationRepository.user.collect{
                Timber.e("User: $it")
                it?.let {
                    user ->
                    _homeUiStateFlow.update {
                            homeUiState ->

                        homeUiState.copy(userName = user.userName ?: "")
                    }
                }
            }
           /* val result = authenticationRepository.getUser()
            result?.let {
                user ->
                _homeUiStateFlow.update {
                    homeUiState ->

                    homeUiState.copy(userName = user.userName ?: "")
                }
            }*/
        }
    }

    fun getRecipesForHomeScreen(){
        viewModelScope.launch {
            _homeUiStateFlow.update {
                homeUiState ->
                homeUiState.copy(loading = true)
            }
            val homeScreenData = formatHomeScreenDataUseCase(selectedRecipeCategory)

            Timber.e("Home screen data: ${homeScreenData.errorMessage}")

            // If both the random recipes and recipes by category are not empty
            if (!homeScreenData.randomRecipes.isNullOrEmpty() && !homeScreenData.recipesByCategory.isNullOrEmpty()){
                // If an error message is also available, display the stale data and show the error message
                if(homeScreenData.errorMessage != null){
                    _homeUiStateFlow.update {
                            homeUiState ->
                        homeUiState.copy(
                            randomRecipes = homeScreenData.randomRecipes,
                            recipesByCategory = homeScreenData.recipesByCategory,
                            showErrorMessage = Pair(true, homeScreenData.errorMessage),
                            shouldKeepSplashScreenOn = false,
                            loading = false
                        )
                    }
                    Timber.e("Home screen data first: ${homeScreenData.errorMessage}")
                } else{
                    // Else, just display the random recipes and recipes by category to the user
                    _homeUiStateFlow.update {
                            homeUiState ->
                        homeUiState.copy(
                            randomRecipes = homeScreenData.randomRecipes,
                            recipesByCategory = homeScreenData.recipesByCategory,
                            shouldKeepSplashScreenOn = false,
                            loading = false
                        )
                    }
                }
            }

            // If either one of the recipes is null(empty), No recipe data should be displayed and the error message should be shown
            if (homeScreenData.randomRecipes.isNullOrEmpty() || homeScreenData.recipesByCategory.isNullOrEmpty()){
                _homeUiStateFlow.update {
                        homeUiState ->
                    homeUiState.copy(
                        randomRecipes = listOf(),
                        recipesByCategory = listOf(),
                        showErrorMessage = Pair(true, homeScreenData.errorMessage),
                        shouldKeepSplashScreenOn = false,
                        loading = false
                    )
                }
                Timber.e("Home screen data second: ${homeScreenData.errorMessage}")
            }

        }
    }

    fun getRecipesByMealType(selectedRecipeCategory: String = "Main Course"){
        getRecipesByMealTypeJob?.cancel()
        _homeUiStateFlow.update {
            homeUiState ->
            homeUiState.copy(startShimmer = true)
        }
        getRecipesByMealTypeJob = viewModelScope.launch {
            delay(500L)
            this@HomeViewModel.selectedRecipeCategory = selectedRecipeCategory
            when(val result = recipeRepository.getRecipesByMealType(mealType = selectedRecipeCategory.lowercase())){
                is OhuneloResult.Success -> {
                    _homeUiStateFlow.update {
                        homeUiState ->
                        homeUiState.copy(
                            recipesByCategory = result.data!!,
                            shouldKeepSplashScreenOn = false,
                            startShimmer = false,
                        )
                    }
                }

                is OhuneloResult.Error -> {
                    _homeUiStateFlow.update {
                            homeUiState ->
                        homeUiState.copy(
                            recipesByCategory = result.data ?: listOf(),
                            showErrorMessage = Pair(true, result.errorMessage),
                            shouldKeepSplashScreenOn = false,
                            startShimmer = false
                        )
                    }
                }
            }
        }
    }

    fun updateGreeting(){
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        when(currentHour){
            in 0..11 -> {_homeUiStateFlow.update { it.copy(userGreetingText = UiText.StringResource(
                R.string.good_morning), userGreetingIcon = UiDrawable(R.drawable.sun_icon)
            ) }}
            in 12..16 -> {_homeUiStateFlow.update { it.copy(userGreetingText =
            UiText.StringResource(R.string.good_afternoon),
                userGreetingIcon = UiDrawable(R.drawable.sun_icon)
            ) }}
            in 17..21 -> {_homeUiStateFlow.update { it.copy(userGreetingText =
            UiText.StringResource(R.string.good_evening),
                userGreetingIcon = UiDrawable(R.drawable.moon_icon)
            ) }}
            else -> {_homeUiStateFlow.update { it.copy(userGreetingText =
            UiText.StringResource(R.string.good_evening),
                userGreetingIcon = UiDrawable(R.drawable.moon_icon)) }}
        }

    }

    fun onErrorMessageShown() {
        _homeUiStateFlow.update {
            homeUiState ->
            homeUiState.copy(
                showErrorMessage = Pair(false, null)
            )
        }
    }
}