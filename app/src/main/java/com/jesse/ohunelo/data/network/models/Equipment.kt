package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Equipment(
    val id: Int,
    val image: String,
    val localizedName: String,
    val name: String
)