package com.jesse.ohunelo.data.model

import android.os.Parcelable
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.util.UiText
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int = 0,
    val analyzedInstructions: List<AnalyzedInstructions> = listOf(),
    val cookingMinutes: Int = 0,
    val creditsText: String = "",
    val extendedIngredients: List<ExtendedIngredient> = listOf(),
    val healthScore: Int = 0,
    val image: String = "",
    val imageType: String = "",
    val instructions: String = "",
    val preparationMinutes: Int = 0,
    val pricePerServing: Double = 0.0,
    val readyInMinutes: Int = 0,
    val servings: Int = 0,
    val sourceName: String = "",
    val title: String = "",
    val weightWatcherSmartPoints: Int = 0,
    val summary: String = "",
    val nutrition: Nutrition? = null
): Parcelable{
    fun formatReadyInMinutes() = UiText.StringResource(R.string.minutes_short, readyInMinutes)

}
