package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Property(
    val amount: Double,
    val name: String,
    val unit: String
)