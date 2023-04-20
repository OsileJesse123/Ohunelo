package com.jesse.ohunelo.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalyzedInstructions(
    val name: String,
    val steps: List<Step>
)
