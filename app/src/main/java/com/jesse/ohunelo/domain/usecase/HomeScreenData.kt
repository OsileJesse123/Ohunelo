package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.util.UiText

data class HomeScreenData(
    val randomRecipes: List<Recipe>? = null,
    val recipesByCategory: List<Recipe>? = null,
    val errorMessage: UiText? = null
)
