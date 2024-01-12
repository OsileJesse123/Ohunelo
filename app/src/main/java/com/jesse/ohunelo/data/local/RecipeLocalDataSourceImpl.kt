package com.jesse.ohunelo.data.local

import com.jesse.ohunelo.data.local.database.RecipeDao
import com.jesse.ohunelo.data.local.models.RecipeEntity
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.HOME_SCREEN_RECIPES_AMOUNT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeLocalDataSourceImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher
): RecipeLocalDataSource {

    override suspend fun insertRecipes(recipes: List<RecipeEntity>) {
        withContext(ioDispatcher){
            recipeDao.insertRecipes(recipes)
        }
    }

    override suspend fun getRandomRecipes(): List<RecipeEntity> {
        return withContext(ioDispatcher){
            recipeDao.getRandomRecipes()
        }
    }

    override suspend fun getRecipesByMealType(mealType: String): List<RecipeEntity> {
        return withContext(defaultDispatcher){
            recipeDao.getAllRecipes().filter {
                it.dishTypes.contains(mealType)
            }.shuffled().take(HOME_SCREEN_RECIPES_AMOUNT)
        }
    }

    override suspend fun updateRecipe(recipe: RecipeEntity) {
        withContext(ioDispatcher){
            recipeDao.updateRecipe(recipe)
        }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeEntity {
        return withContext(ioDispatcher){
            recipeDao.getRecipe(recipeId)
        }
    }
}