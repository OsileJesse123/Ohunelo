package com.jesse.ohunelo.data.model

import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.ExtendedIngredient

data class Recipe(
    val id: Int,
    val analyzedInstructions: List<AnalyzedInstructions>,
    val cookingMinutes: Int,
    val creditsText: String,
    val extendedIngredients: List<ExtendedIngredient>,
    val healthScore: Int,
    val image: String,
    val imageType: String,
    val instructions: String,
    val preparationMinutes: Int,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceName: String,
    val title: String,
    val weightWatcherSmartPoints: Int
){
    fun formatReadyInMinutes() = "$readyInMinutes Min"
    fun formatHealthScore() = "$healthScore Pts"
}
