package com.jesse.ohunelo.data.model

import android.content.Context
import android.os.Parcelable
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.CaloricBreakdown
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val weightWatcherSmartPoints: Int,
    val caloricBreakdown: CaloricBreakdown,
    val summary: String
): Parcelable{
    fun formatReadyInMinutes() = "$readyInMinutes Min"
    fun formatHealthScore() = "$healthScore Pts"
    fun formatCarbsPercent(context: Context) =
        context.getString(R.string.carbs_percent, caloricBreakdown.percentCarbs.toInt())
    fun formatProteinPercent(context: Context) =
        context.getString(R.string.protein_percent, caloricBreakdown.percentProtein.toInt())
    fun formatFatPercent(context: Context) =
        context.getString(R.string.fat_percent, caloricBreakdown.percentFat.toInt())
}
