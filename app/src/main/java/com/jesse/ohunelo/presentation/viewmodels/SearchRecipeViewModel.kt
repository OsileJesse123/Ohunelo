package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val RECIPES_PER_PAGE = 20

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    val recipes: Flow<PagingData<Recipe>> = Pager(
        config = PagingConfig(pageSize = RECIPES_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { recipeRepository.recipePagingSource() }
    ).flow
        .cachedIn(viewModelScope)
}