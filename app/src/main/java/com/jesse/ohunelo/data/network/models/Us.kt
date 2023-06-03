package com.jesse.ohunelo.data.network.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Us(
    val amount: Double,
    val unitLong: String,
    val unitShort: String
): Parcelable