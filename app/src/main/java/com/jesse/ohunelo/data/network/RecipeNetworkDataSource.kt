package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.data.network.models.RecipeResponse
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse

interface RecipeNetworkDataSource {

    suspend fun getRandomRecipes(): RecipesByMealTypeResponse
    suspend fun getRecipesByMealType(mealType: String): RecipesByMealTypeResponse

    suspend fun getRecipes(sort: String = "", mealType: String = "", offset: Int = 0): RecipesByMealTypeResponse
}