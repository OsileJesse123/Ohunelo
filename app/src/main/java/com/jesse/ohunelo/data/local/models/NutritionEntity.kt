package com.jesse.ohunelo.data.local.models

import com.jesse.ohunelo.data.model.Nutrition

data class NutritionEntity(
    val id: Int,
    val calories: String,
    val carbs: String,
    val expires: Long,
    val fat: String,
    val protein: String
){
    fun toNutrition(): Nutrition = Nutrition(
        id, calories, carbs, expires, fat, protein
    )
}
