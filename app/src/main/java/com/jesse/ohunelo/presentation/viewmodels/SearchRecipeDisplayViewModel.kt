package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchRecipeDisplayViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    val recipes: Flow<PagingData<Recipe>>

    private val queryFlow: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        val search = queryFlow.onStart { emit("abc") }

        recipes = search.flatMapLatest {
            searchQuery ->
            recipeRepository.getPagedRecipes(searchQuery = searchQuery)
        }.cachedIn(viewModelScope)
    }

    fun searchRecipes(searchQuery: String){
        Timber.e("Search recipes gets Called")
        viewModelScope.launch {
            if(searchQuery.isNotEmpty() && searchQuery.isNotBlank()){
                queryFlow.emit(searchQuery)
            }
        }
    }
}