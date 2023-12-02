package com.jesse.ohunelo.data.network

import com.jesse.ohunelo.BuildConfig
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.models.RecipesResponse
import com.jesse.ohunelo.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RecipeNetworkDataSourceImpl @Inject constructor(
    private val spoonacularService: SpoonacularService,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
) : RecipeNetworkDataSource {

    override suspend fun getRandomRecipes(): RecipesResponse = withContext(ioDispatcher){
        spoonacularService.getRandomRecipes(apiKey = BuildConfig.API_KEY)
    }

    override suspend fun getRecipesByMealType(mealType: String): RecipesByMealTypeResponse = withContext(ioDispatcher){
        spoonacularService.getRecipesByMealType(mealType = mealType)
    }

}