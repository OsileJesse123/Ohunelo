package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductMatch(
    val averageRating: Double,
    val description: String?,
    val id: Int,
    val imageUrl: String,
    val link: String,
    val price: String,
    val ratingCount: Double,
    val score: Double,
    val title: String
)