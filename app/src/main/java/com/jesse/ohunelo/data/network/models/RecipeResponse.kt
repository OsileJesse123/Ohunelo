package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.data.local.models.NutritionEntity
import com.jesse.ohunelo.data.local.models.RecipeEntity
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.util.RecipeImageSize
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeResponse(
    val aggregateLikes: Int = 0,
    val analyzedInstructions: List<AnalyzedInstructions> = listOf(),
    val cheap: Boolean = false,
    val cookingMinutes: Int = 0,
    val creditsText: String = "",
    val cuisines: List<String> = listOf(),
    val dairyFree: Boolean = false,
    val diets: List<String> = listOf(),
    val dishTypes: List<String> = listOf(),
    val extendedIngredients: List<ExtendedIngredient> = listOf(),
    val gaps: String = "",
    val glutenFree: Boolean = false,
    val healthScore: Int = 0,
    val id: Int,
    val image: String = "",
    val imageType: String = "",
    val instructions: String = "",
    val license: String = "",
    val lowFodmap: Boolean = false,
    val occasions: List<Any> = listOf(),
    val originalId: Any? = null,
    val preparationMinutes: Int = 0,
    val pricePerServing: Double = 0.0,
    val readyInMinutes: Int = 0,
    val servings: Int = 0,
    val sourceName: String = "",
    val sourceUrl: String = "",
    val spoonacularSourceUrl: String = "",
    val summary: String = "",
    val sustainable: Boolean = false,
    val title: String = "",
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val veryHealthy: Boolean = false,
    val veryPopular: Boolean = false,
    val weightWatcherSmartPoints: Int,
    val nutrition: NutritionResponse? = null
){
    fun toRecipeEntity() = RecipeEntity(
        id = id,
        analyzedInstructions = analyzedInstructions,
        cookingMinutes = cookingMinutes,
        creditsText = creditsText,
        extendedIngredients = extendedIngredients,
        healthScore = healthScore,
        image = "https://spoonacular.com/recipeImages/$id-${RecipeImageSize.SIZE7.size}.$imageType",
        imageType = imageType,
        instructions = instructions,
        preparationMinutes = preparationMinutes,
        pricePerServing = pricePerServing,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceName = sourceName,
        title = title,
        weightWatcherSmartPoints = weightWatcherSmartPoints,
        summary = summary,
        nutritionEntity = nutrition?.toNutritionEntity(id),
        dishTypes = dishTypes
    )

    fun toRecipe() = Recipe(
        id = id,
        analyzedInstructions = analyzedInstructions,
        cookingMinutes = cookingMinutes,
        creditsText = creditsText,
        extendedIngredients = extendedIngredients,
        healthScore = healthScore,
        image = "https://spoonacular.com/recipeImages/$id-${RecipeImageSize.SIZE7.size}.$imageType",
        imageType = imageType,
        instructions = instructions,
        preparationMinutes = preparationMinutes,
        pricePerServing = pricePerServing,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceName = sourceName,
        title = title,
        weightWatcherSmartPoints = weightWatcherSmartPoints,
        summary = summary,
        nutrition = nutrition?.toNutrition(id),
    )
}