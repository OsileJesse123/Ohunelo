package com.jesse.ohunelo.data.model

import android.content.Context
import android.os.Parcelable
import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.UiText
import kotlinx.parcelize.Parcelize

@Parcelize
data class Nutrition(
    val id: Int,
    val calories: String,
    val carbs: String,
    val fat: String,
    val protein: String
): Parcelable{
    fun formatCarbs(): UiText = UiText.StringResource(R.string.carbs, carbs)
    fun formatProtein() = UiText.StringResource(R.string.protein, protein)
    fun formatFat() = UiText.StringResource(R.string.fat, fat)
    fun formatCalories() = UiText.StringResource(R.string.calories, calories)
}
