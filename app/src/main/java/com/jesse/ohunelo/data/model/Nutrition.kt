package com.jesse.ohunelo.data.model

import android.content.Context
import com.jesse.ohunelo.R

data class Nutrition(
    val id: Int,
    val calories: String,
    val carbs: String,
    val expires: Long,
    val fat: String,
    val protein: String
){
    fun formatCarbs(context: Context) =
        context.getString(R.string.carbs, carbs)
    fun formatProtein(context: Context) =
        context.getString(R.string.protein, protein)
    fun formatFat(context: Context) =
        context.getString(R.string.fat, fat)
}
