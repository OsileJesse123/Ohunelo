package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.data.model.Recipe

data class RecipeDetailsUiState(
    val recipe: Recipe = Recipe(),
    val isLoading: Boolean = true,
    val collapseBottomSheet: Boolean = false
)