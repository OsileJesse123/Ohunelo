package com.jesse.ohunelo.data.local.models

import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.CaloricBreakdown
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.data.network.models.WinePairing

data class RecipeEntity(
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
    val weightWatcherSmartPoints: Int,
    val summary: String
){
    fun toRecipe() = Recipe(
        id = id,
        analyzedInstructions = analyzedInstructions,
        cookingMinutes = cookingMinutes,
        creditsText = creditsText,
        extendedIngredients = extendedIngredients,
        healthScore = healthScore,
        image = image,
        imageType = imageType,
        instructions = instructions,
        preparationMinutes = preparationMinutes,
        pricePerServing = pricePerServing,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceName = sourceName,
        title = title,
        weightWatcherSmartPoints = weightWatcherSmartPoints,
        summary = summary
    )
}
