package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Nutrition(
    val caloricBreakdown: CaloricBreakdown,
    val flavonoids: List<Flavonoid>,
    val ingredients: List<Ingredients>,
    val nutrients: List<Nutrients>,
    val properties: List<Property>,
    val weightPerServing: WeightPerServing
)