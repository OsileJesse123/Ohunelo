package com.jesse.ohunelo.data.local.data_source

import com.jesse.ohunelo.data.local.models.RecipeEntity


interface RecipeLocalDataSource {

    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    suspend fun getRandomRecipes(): List<RecipeEntity>

    suspend fun getRecipesByMealType(mealType: String): List<RecipeEntity>

    suspend fun updateRecipe(recipe: RecipeEntity)

    suspend fun getRecipe(recipeId: Int): RecipeEntity
}