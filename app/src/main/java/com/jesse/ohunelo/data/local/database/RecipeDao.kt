package com.jesse.ohunelo.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jesse.ohunelo.data.local.models.RecipeEntity

@Dao
interface RecipeDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Query("SELECT * FROM recipe ORDER BY RANDOM() LIMIT 20")
    suspend fun getRandomRecipes(): List<RecipeEntity>

    @Query("SELECT * FROM recipe WHERE :recipeId = id")
    suspend fun getRecipe(recipeId: Int): RecipeEntity

}