package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.data.local.models.RecipeEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeResponse(
    val aggregateLikes: Int,
    val analyzedInstructions: List<AnalyzedInstructions>,
    val cheap: Boolean,
    val cookingMinutes: Int,
    val creditsText: String,
    val cuisines: List<String>,
    val dairyFree: Boolean,
    val diets: List<String>,
    val dishTypes: List<String>,
    val extendedIngredients: List<ExtendedIngredient>,
    val gaps: String,
    val glutenFree: Boolean,
    val healthScore: Int,
    val id: Int,
    val image: String,
    val imageType: String,
    val instructions: String,
    val license: String,
    val lowFodmap: Boolean,
    val occasions: List<String>,
    val originalId: Any?,
    val preparationMinutes: Int,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceName: String,
    val sourceUrl: String,
    val spoonacularSourceUrl: String,
    val summary: String,
    val sustainable: Boolean,
    val title: String,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
    val veryPopular: Boolean,
    val weightWatcherSmartPoints: Int,
    val winePairing: WinePairing
){
    fun toRecipeEntity() = RecipeEntity(
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