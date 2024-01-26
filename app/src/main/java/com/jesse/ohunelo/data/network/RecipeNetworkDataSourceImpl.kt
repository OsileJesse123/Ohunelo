package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.BuildConfig
import com.jesse.ohunelo.data.network.models.RecipeResponse
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse
import com.jesse.ohunelo.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


class RecipeNetworkDataSourceImpl @Inject constructor(
    private val spoonacularService: SpoonacularService,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
) : RecipeNetworkDataSource {

    override suspend fun getRandomRecipes(): RecipesByMealTypeResponse = withContext(ioDispatcher){
        spoonacularService.getRecipes(sort = "random")
    }

    override suspend fun getRecipesByMealType(mealType: String): RecipesByMealTypeResponse = withContext(ioDispatcher){
        spoonacularService.getRecipes(mealType = mealType)
    }

    override suspend fun getRecipes(sort: String, mealType: String, offset: Int, number: Int): RecipesByMealTypeResponse = withContext(ioDispatcher){
        spoonacularService.getRecipes(mealType = mealType, number = number, offset = offset)
    }


}