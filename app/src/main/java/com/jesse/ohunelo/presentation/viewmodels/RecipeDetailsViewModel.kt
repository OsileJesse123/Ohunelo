package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.jesse.ohunelo.data.model.Nutrition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(): ViewModel() {

    private val _nutrition: MutableStateFlow<Nutrition?> = MutableStateFlow(null)
    val nutrition get() = _nutrition.asStateFlow()

    fun getNutrition(recipeId: Int){
        _nutrition.update {
            Nutrition(recipeId, calories = "316", carbs = "49g", fat = "12g", protein = "3g",
                expires = 1L)
        }
    }
}