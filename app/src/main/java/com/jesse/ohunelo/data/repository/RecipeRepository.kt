package com.jesse.ohunelo.data.repository

import androidx.paging.PagingData
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.OhuneloResult
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun getRandomRecipes(): OhuneloResult<List<Recipe>>

    suspend fun getRecipesByMealType(mealType: String): OhuneloResult<List<Recipe>>

    fun getPagedRecipes(): Flow<PagingData<Recipe>>
}