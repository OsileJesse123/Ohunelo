package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Nutrients(
    val amount: Double,
    val name: String,
    val percentOfDailyNeeds: Double,
    val unit: String
)