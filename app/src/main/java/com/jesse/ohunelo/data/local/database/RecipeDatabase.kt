package com.jesse.ohunelo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jesse.ohunelo.data.local.database.typeconverters.AnalyzedInstructionsListTypeConverter
import com.jesse.ohunelo.data.local.database.typeconverters.ExtendedIngredientListTypeConverter
import com.jesse.ohunelo.data.local.database.typeconverters.NutritionEntityTypeConverter
import com.jesse.ohunelo.data.local.database.typeconverters.StringListTypeConverter
import com.jesse.ohunelo.data.local.models.NotificationEntity
import com.jesse.ohunelo.data.local.models.NutritionEntity
import com.jesse.ohunelo.data.local.models.RecipeEntity

private const val DATABASE_VERSION = 1

@Database(entities = [RecipeEntity::class, NutritionEntity::class], version = DATABASE_VERSION)
@TypeConverters(NutritionEntityTypeConverter::class, ExtendedIngredientListTypeConverter::class, AnalyzedInstructionsListTypeConverter::class, StringListTypeConverter::class)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
}