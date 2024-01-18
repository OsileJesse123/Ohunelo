package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(): ViewModel() {

    private val ingredients: MutableList<ExtendedIngredient> = mutableListOf()

    private val _ingredientsFlow: MutableStateFlow<List<ExtendedIngredient>> = MutableStateFlow(listOf())
    val ingredientsFlow get() = _ingredientsFlow.asStateFlow()


    fun updateIngredients(listOfIngredients: List<ExtendedIngredient>){
        ingredients.addAll(listOfIngredients)
        fetchIngredients(1)
    }

    fun fetchIngredients(count: Int){
        if(ingredients.size > count * 7){
            _ingredientsFlow.update {
                ingredients.take(count * 7)
            }
        } else{
            _ingredientsFlow.update {
                ingredients
            }
        }
    }
}