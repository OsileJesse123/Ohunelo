package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Step(
    val equipment: List<Equipment>,
    val ingredients: List<Ingredient>,
    val number: Int,
    val step: String
)