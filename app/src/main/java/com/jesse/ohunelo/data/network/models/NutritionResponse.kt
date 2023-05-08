package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.data.local.models.NutritionEntity

data class NutritionResponse(
    val bad: List<Bad>,
    val caloricBreakdown: CaloricBreakdown,
    val calories: String,
    val carbs: String,
    val expires: Long,
    val fat: String,
    val flavonoids: List<Flavonoids>,
    val good: List<Good>,
    val ingredients: List<Ingredients>,
    val isStale: Boolean,
    val nutrients: List<Nutrients>,
    val properties: List<Property>,
    val protein: String,
    val weightPerServing: WeightPerServing
){
    fun toNutritionEntity(id: Int): NutritionEntity = NutritionEntity(
        id, calories, carbs, expires, fat, protein)
}