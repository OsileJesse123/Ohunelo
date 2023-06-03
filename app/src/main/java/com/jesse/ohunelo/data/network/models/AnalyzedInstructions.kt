package com.jesse.ohunelo.data.network.models

import android.content.Context
import android.os.Parcelable
import com.jesse.ohunelo.R
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class AnalyzedInstructions(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val steps: List<Step>
): Parcelable{
    fun formatStepsSize(context: Context): String{
        return context.resources.getQuantityString(R.plurals.number_of_steps_available, steps.size, steps.size)
    }

    fun formatName(context: Context): String{
        return name.ifEmpty { context.getString(R.string.analyzed_instruction) }
    }
}
