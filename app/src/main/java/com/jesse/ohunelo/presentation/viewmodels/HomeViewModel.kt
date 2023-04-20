package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.presentation.uistates.HomeUiState
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _homeUiStateFlow: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiStateFlow: StateFlow<HomeUiState> get() = _homeUiStateFlow.asStateFlow()

    // todo: this acts a source of recipe data for now and would be removed soon
   private var recipes = listOf<Recipe>()

    // todo: when data layer is properly setup changes will be made here
    init {
        updateGreeting()
        recipes =  listOf(
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
                healthScore = 5,
                image = "image",
                imageType = "imageType",
                instructions = "instructions",
                preparationMinutes = 20,
                pricePerServing = 100.00,
                readyInMinutes = 20,
                servings = 2,
                sourceName = "Anthony Joshua",
                title = "Asian Chickpea Lettuce Wraps",
                weightWatcherSmartPoints = 33
            ),
        )
        _homeUiStateFlow.update {
            state ->
            state.copy(randomRecipes = recipes, recipesByCategory = recipes)
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
}