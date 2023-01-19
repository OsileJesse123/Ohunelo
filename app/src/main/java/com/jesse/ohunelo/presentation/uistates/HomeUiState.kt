package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText

data class HomeUiState(
    val userGreetingText: UiText = UiText.StringResource(R.string.good_morning),
    val userGreetingIcon: UiDrawable = UiDrawable(R.drawable.sun_icon),
    val randomRecipes: List<Recipe> = listOf(),
    val recipesByCategory: List<Recipe> = listOf()
)
