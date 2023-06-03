package com.jesse.ohunelo.data.network.models

import android.content.Context
import android.os.Parcelable
import com.jesse.ohunelo.R
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Step(
    val equipment: List<Equipment>,
    val ingredients: List<Ingredient>,
    val number: Int,
    val step: String
): Parcelable{
    fun getStepNumberString(context: Context): String {
        return context.getString(R.string.step_number_text, number)
    }

}