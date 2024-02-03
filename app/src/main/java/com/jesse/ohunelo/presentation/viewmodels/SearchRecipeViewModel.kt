package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    var recipes: Flow<PagingData<Recipe>>? = null
        private set

    fun updateRecipes(){
        recipes = recipeRepository.getPagedRecipes(sort = "random").cachedIn(viewModelScope)
    }
}