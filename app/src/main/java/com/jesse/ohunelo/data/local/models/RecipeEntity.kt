package com.jesse.ohunelo.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.CaloricBreakdown
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.data.network.models.WinePairing

@Entity(tableName = "recipe")
data class RecipeEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "analyzed_instructions")
    val analyzedInstructions: List<AnalyzedInstructions>,
    @ColumnInfo(name = "cooking_minutes")
    val cookingMinutes: Int,
    @ColumnInfo(name = "credits_text")
    val creditsText: String,
    @ColumnInfo(name = "extended_ingredients")
    val extendedIngredients: List<ExtendedIngredient>,
    @ColumnInfo(name = "health_score")
    val healthScore: Int,
    val image: String,
    @ColumnInfo(name = "image_type")
    val imageType: String,
    val instructions: String,
    @ColumnInfo(name = "preparation_minutes")
    val preparationMinutes: Int,
    @ColumnInfo(name = "price_per_serving")
    val pricePerServing: Double,
    @ColumnInfo(name = "ready_in_minutes")
    val readyInMinutes: Int,
    val servings: Int,
    @ColumnInfo(name = "source_name")
    val sourceName: String,
    val title: String,
    @ColumnInfo(name = "weight_watcher_smart_points")
    val weightWatcherSmartPoints: Int,
    val summary: String,
    @ColumnInfo(name = "nutrition_entity")
    val nutritionEntity: NutritionEntity,
    @ColumnInfo(name = "dish_types")
    val dishTypes: List<String>
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
        summary = summary,
        nutrition = nutritionEntity.toNutrition()
    )
}
