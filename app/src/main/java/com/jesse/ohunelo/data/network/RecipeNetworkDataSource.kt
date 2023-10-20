package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse

interface RecipeNetworkDataSource {

    suspend fun getRandomRecipes(): RecipesResponse
    suspend fun getRecipesByMealType(mealType: String): RecipesByMealTypeResponse
}