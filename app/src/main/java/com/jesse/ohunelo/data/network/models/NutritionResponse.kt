package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.data.local.models.NutritionEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NutritionResponse(
    val caloricBreakdown: CaloricBreakdown? = null,
    val flavonoids: List<Flavonoids> = listOf(),
    val ingredients: List<Ingredients> = listOf(),
    val nutrients: List<Nutrients> = listOf(),
    val properties: List<Property> = listOf(),
    val weightPerServing: WeightPerServing? = null
){
    fun toNutritionEntity(id: Int): NutritionEntity = NutritionEntity(
        id, getNutrientAmount("Calories"), getNutrientAmount("Carbohydrates"), getNutrientAmount("Fat"), getNutrientAmount("Protein"))

    private fun getNutrientAmount(nutrientName: String): String{
        val amount = nutrients.find { nutrients -> nutrients.name == nutrientName }?.amount
        return amount?.toString() ?: "0"
    }
}