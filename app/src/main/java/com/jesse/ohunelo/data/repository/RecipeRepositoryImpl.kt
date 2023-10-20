package com.jesse.ohunelo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.RecipePagingSource
import com.jesse.ohunelo.data.local.models.NutritionEntity
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

private const val RECIPES_PER_PAGE = 20
class RecipeRepositoryImpl @Inject constructor(
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher
): RecipeRepository {
    override suspend fun getRandomRecipes(): OhuneloResult<List<Recipe>> {
         return try {
            val result = recipeNetworkDataSource.getRandomRecipes()
            val recipes = withContext(defaultDispatcher){
                result.recipes.map {
                    recipeResponse ->
                    recipeResponse.toRecipeEntity(
                        NutritionEntity(
                            id = recipeResponse.id,
                            calories = "0",
                            carbs = "0",
                            expires = 0,
                            fat = "0",
                            protein = "0"
                        )
                    ).toRecipe()
                }
            }
            Timber.e("Random recipes: $recipes")
            OhuneloResult.Success(recipes)
        }
        catch (e: Exception){
            Timber.e("Random recipes fetch failed, error: $e, error message: ${e.localizedMessage}")
            OhuneloResult.Error(UiText.StringResource(R.string.failed_to_get_recipes))
        }
    }

    override suspend fun getRecipesByMealType(
        mealType: String
    ): OhuneloResult<List<Recipe>> {
        return try {
            val result = recipeNetworkDataSource.getRecipesByMealType(mealType)
            val recipes = withContext(defaultDispatcher){
                result.results.map {
                        recipeResponse ->
                    recipeResponse.toRecipeEntity(
                        NutritionEntity(
                            id = recipeResponse.id,
                            calories = "0",
                            carbs = "0",
                            expires = 0,
                            fat = "0",
                            protein = "0"
                        )
                    ).toRecipe()
                }
            }
            Timber.e("Recipes by meal type: $recipes")
            OhuneloResult.Success(recipes)
        }
        catch (e: Exception){
            Timber.e("Recipes by meal type fetch failed, error: $e, error message: ${e.localizedMessage}")
            OhuneloResult.Error(UiText.StringResource(R.string.failed_to_get_recipes))
        }
    }


    override fun getPagedRecipes(): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = RECIPES_PER_PAGE, enablePlaceholders = false),
            pagingSourceFactory = { RecipePagingSource() }
        ).flow
    }
}