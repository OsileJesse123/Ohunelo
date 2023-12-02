package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipesResponse(
    val recipes: List<RecipeResponse>
)