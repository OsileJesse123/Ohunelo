package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.domain.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * This class is responsible for fetching the random recipes and recipes by category, sorting them and sending it down to the presentation layer
 */
class FormatHomeScreenDataUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository,
){

    suspend operator fun invoke(selectedRecipeCategory: String): HomeScreenData = coroutineScope {
        var homeScreenData = HomeScreenData()
        val deferredRandomRecipes = async{ recipeRepository.getRandomRecipes() }
        val deferredRecipesByCategory = async{recipeRepository.getRecipesByMealType(selectedRecipeCategory.lowercase())}

        val (randomRecipes, recipesByCategory) = awaitAll(deferredRandomRecipes, deferredRecipesByCategory)

        homeScreenData = when(randomRecipes){
            is OhuneloResult.Success ->{
                homeScreenData.copy(randomRecipes = randomRecipes.data)
            }

            is OhuneloResult.Error -> {
                homeScreenData.copy(randomRecipes = randomRecipes.data, error = randomRecipes.error)
            }
        }

        homeScreenData = when(recipesByCategory){
            is OhuneloResult.Success ->{
                homeScreenData.copy(recipesByCategory = recipesByCategory.data)
            }

            is OhuneloResult.Error -> {
                homeScreenData.copy(recipesByCategory = recipesByCategory.data, error = recipesByCategory.error)
            }
        }

        homeScreenData
    }

}