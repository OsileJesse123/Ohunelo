package com.jesse.ohunelo.data.local.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions

class AnalyzedInstructionsListTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAnalyzedInstructionsList(instructions: List<AnalyzedInstructions>): String {
        return gson.toJson(instructions)
    }

    @TypeConverter
    fun toAnalyzedInstructionsList(json: String): List<AnalyzedInstructions> {
        // this is used to preserve the generic type information
        val type = object : TypeToken<List<AnalyzedInstructions>>() {}.type
        return gson.fromJson(json, type)
    }
}