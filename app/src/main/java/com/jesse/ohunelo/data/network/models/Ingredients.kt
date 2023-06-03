package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ingredients(
    val amount: Double,
    val id: Int,
    val name: String,
    val nutrients: List<Nutrients>,
    val unit: String
)