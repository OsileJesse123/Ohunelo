package com.jesse.ohunelo.data.local.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesse.ohunelo.data.network.models.ExtendedIngredient


class StringListTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        // this is used to preserve the generic type information
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}