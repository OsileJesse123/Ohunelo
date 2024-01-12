package com.jesse.ohunelo.domain.usecase

import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.RecipeRepository
import javax.inject.Inject

/**
 * This class is responsible for fetching the random recipes and recipes by category, sorting them and sending it down to the presentation layer
 */
class FormatHomeScreenDataUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository,
){

    private var homeScreenData = HomeScreenData()

    suspend operator fun invoke(selectedRecipeCategory: String): HomeScreenData{
        val randomRecipes = recipeRepository.getRandomRecipes()
        val recipesByCategory = recipeRepository.getRecipesByMealType(selectedRecipeCategory.lowercase())

        homeScreenData = when(randomRecipes){
            is OhuneloResult.Success ->{
                homeScreenData.copy(randomRecipes = randomRecipes.data)
            }

            is OhuneloResult.Error -> {
                homeScreenData.copy(randomRecipes = randomRecipes.data, errorMessage = randomRecipes.errorMessage)
            }
        }

        homeScreenData = when(recipesByCategory){
            is OhuneloResult.Success ->{
                homeScreenData.copy(recipesByCategory = recipesByCategory.data)
            }

            is OhuneloResult.Error -> {
                homeScreenData.copy(recipesByCategory = recipesByCategory.data, errorMessage = recipesByCategory.errorMessage)
            }
        }

        return homeScreenData
    }

}