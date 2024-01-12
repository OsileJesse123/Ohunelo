package com.jesse.ohunelo.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jesse.ohunelo.data.model.Nutrition

@Entity(tableName = "nutrition")
data class NutritionEntity(
    @PrimaryKey
    val id: Int,
    val calories: String,
    val carbs: String,
    val fat: String,
    val protein: String
){
    fun toNutrition(): Nutrition = Nutrition(
        id, calories, carbs, fat, protein
    )
}
