package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.data.network.models.RecipeResponse
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse

interface RecipeNetworkDataSource {

    suspend fun getRecipes(sort: String = "", mealType: String = "", offset: Int = 0, number: Int = 0): RecipesByMealTypeResponse
}