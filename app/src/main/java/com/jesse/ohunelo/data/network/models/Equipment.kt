package com.jesse.ohunelo.data.network.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class
Equipment(
    val id: Int,
    val image: String,
    val localizedName: String,
    val name: String
): Parcelable