package com.jesse.ohunelo.data.repository

import androidx.paging.PagingData
import com.jesse.ohunelo.data.RecipePagingSource
import com.jesse.ohunelo.data.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    fun getPagedRecipes(): Flow<PagingData<Recipe>>
}