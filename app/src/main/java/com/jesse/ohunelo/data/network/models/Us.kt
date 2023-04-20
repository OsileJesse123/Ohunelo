package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Us(
    val amount: Double,
    val unitLong: String,
    val unitShort: String
)