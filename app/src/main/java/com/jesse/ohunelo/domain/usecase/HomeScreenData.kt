package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.data.model.Recipe

data class HomeScreenData(
    val randomRecipes: List<Recipe>? = null,
    val recipesByCategory: List<Recipe>? = null,
    val error: Exception? = null
)
