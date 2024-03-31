package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.data.network.models.RecipeResponse
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse
import com.jesse.ohunelo.util.HOME_SCREEN_RECIPES_AMOUNT

interface RecipeNetworkDataSource {

    suspend fun getRecipes(sort: String = "", mealType: String = "", offset: Int = 0, number: Int = HOME_SCREEN_RECIPES_AMOUNT, searchQuery: String = ""): RecipesByMealTypeResponse
}