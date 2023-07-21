package com.jesse.ohunelo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jesse.ohunelo.data.RecipePagingSource
import com.jesse.ohunelo.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val RECIPES_PER_PAGE = 20
class RecipeRepositoryImpl @Inject constructor(): RecipeRepository {

    override fun getPagedRecipes(): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = RECIPES_PER_PAGE, enablePlaceholders = false),
            pagingSourceFactory = { RecipePagingSource() }
        ).flow
    }
}