package com.jesse.ohunelo.data.network.data_source

import com.jesse.ohunelo.data.network.models.RandomFoodJokeResponse
import com.jesse.ohunelo.data.network.models.RandomFoodTriviaResponse
import com.jesse.ohunelo.data.network.models.RecipesByMealTypeResponse
import com.jesse.ohunelo.data.network.service.SpoonacularService
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.HOME_SCREEN_RECIPES_AMOUNT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RecipeNetworkDataSource @Inject constructor(
    private val spoonacularService: SpoonacularService
) {
    suspend fun getRecipes(sort: String = "", mealType: String = "", offset: Int = 0, number: Int = HOME_SCREEN_RECIPES_AMOUNT, searchQuery: String = ""): RecipesByMealTypeResponse =
        spoonacularService.getRecipes(mealType = mealType, number = number, offset = offset, sort = sort, searchQuery = searchQuery)


    suspend fun getRandomFoodJoke(): RandomFoodJokeResponse =
        spoonacularService.getRandomFoodJoke()

    suspend fun getRandomFoodTrivia(): RandomFoodTriviaResponse =
        spoonacularService.getRandomFoodTrivia()
}