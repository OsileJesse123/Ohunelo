package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.BuildConfig
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularService {

    @GET("random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY,
        @Query("number") number: Int = 20
    ): RecipesResponse

    @GET("complexSearch")
    suspend fun getRecipesByMealType(
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY,
        @Query("number") number: Int = 20,
        @Query("type") mealType: String,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("addRecipeNutrition") addRecipeNutrition: Boolean = true,
        @Query("fillIngredients") fillIngredients: Boolean = true
    ): RecipesByMealTypeResponse
}