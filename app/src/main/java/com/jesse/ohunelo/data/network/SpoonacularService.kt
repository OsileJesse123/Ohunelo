package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.util.HOME_SCREEN_RECIPES_AMOUNT
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularService {

    @GET("complexSearch")
    suspend fun getRecipes(
        @Query("number") number: Int = HOME_SCREEN_RECIPES_AMOUNT,
        @Query("type") mealType: String = "",
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("addRecipeNutrition") addRecipeNutrition: Boolean = true,
        @Query("fillIngredients") fillIngredients: Boolean = true,
        @Query("sort") sort: String = ""
    ): RecipesByMealTypeResponse
}