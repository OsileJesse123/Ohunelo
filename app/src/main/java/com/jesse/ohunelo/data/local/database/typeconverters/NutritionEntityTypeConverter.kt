package com.jesse.ohunelo.data.local.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jesse.ohunelo.data.local.models.NutritionEntity

class NutritionEntityTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromNutritionEntity(nutritionEntity: NutritionEntity?): String {
        return if(nutritionEntity == null){
            gson.toJson(NutritionEntity(0, "", "", "", ""))
        } else {
            gson.toJson(nutritionEntity)
        }
    }

    @TypeConverter
    fun toNutritionEntity(json: String?): NutritionEntity {
        return gson.fromJson(json, NutritionEntity::class.java)
    }
}