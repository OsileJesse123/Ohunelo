package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeightPerServing(
    val amount: Int,
    val unit: String
)