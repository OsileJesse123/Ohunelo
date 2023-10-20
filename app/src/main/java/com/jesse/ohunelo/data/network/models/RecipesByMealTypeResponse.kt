package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipesByMealTypeResponse(
    val number: Int,
    val offset: Int,
    val results: List<RecipeResponse>,
    val totalResults: Int
)