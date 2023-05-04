package com.jesse.ohunelo.data.network.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CaloricBreakdown(
    val percentCarbs: Double,
    val percentFat: Double,
    val percentProtein: Double
): Parcelable