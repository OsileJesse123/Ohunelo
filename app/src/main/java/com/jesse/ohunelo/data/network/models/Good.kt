package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Good(
    val amount: String,
    val indented: Boolean,
    val percentOfDailyNeeds: Double,
    val title: String
)