package com.jesse.ohunelo.data.local.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesse.ohunelo.data.network.models.ExtendedIngredient

class ExtendedIngredientListTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromExtendedIngredientList(ingredients: List<ExtendedIngredient>): String {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toExtendedIngredientList(json: String): List<ExtendedIngredient> {
        // this is used to preserve the generic type information
        val type = object : TypeToken<List<ExtendedIngredient>>() {}.type
        return gson.fromJson(json, type)
    }
}