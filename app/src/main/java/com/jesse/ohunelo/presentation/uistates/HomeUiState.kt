package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.util.UiText

data class HomeUiState(
    val randomRecipes: List<Recipe> = listOf(),
    val recipesByCategory: List<Recipe> = listOf(),
    /** first is a boolean determining whether or not an error message should be shown.
     *
     *  second is a nullable UiText, this is the error message to be displayed.
     * **/
    val showErrorMessage: Pair<Boolean, UiText?> = Pair(false, null),
    val shouldKeepSplashScreenOn: Boolean = true,
    /**
     * This determines whether or not the loader for the home screen should show
     */
    val loading: Boolean = false,
    /**
     * This determines whether or not to show the shimmer effect for recipes by category items
     */
    val startShimmer: Boolean = false
)
