package com.jesse.ohunelo.data.network.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ExtendedIngredient(
    val aisle: String?,
    val amount: Double,
    val consistency: String,
    val id: Int,
    val image: String?,
    val measures: Measures,
    val meta: List<String>,
    val name: String,
    val nameClean: String?,
    val original: String,
    val originalName: String,
    val unit: String
): Parcelable{
    fun formatIngredientQuantity(): String = "${measures.us.amount.toInt()} $unit"
}