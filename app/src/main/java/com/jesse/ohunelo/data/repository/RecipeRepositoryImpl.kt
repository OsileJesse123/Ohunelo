package com.jesse.ohunelo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.RecipePagingSource
import com.jesse.ohunelo.data.local.RecipeLocalDataSource
import com.jesse.ohunelo.data.local.models.NutritionEntity
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.RECIPE_PAGE_SIZE
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

private const val RECIPES_PER_PAGE = 20
class RecipeRepositoryImpl @Inject constructor(
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
): RecipeRepository {
    override suspend fun getRandomRecipes(): OhuneloResult<List<Recipe>> {
         return try {
             // Get the recipes from api
            val result = recipeNetworkDataSource.getRandomRecipes()
             // Convert recipes response to recipe entities
            val recipeEntities = withContext(defaultDispatcher){
                result.results.map {
                    recipeResponse ->
                    recipeResponse.toRecipeEntity()
                }
            }
             // Insert recipe entities into database
            recipeLocalDataSource.insertRecipes(recipeEntities)
             // Get the recipe entities from the database and convert to recipes
            val recipes = withContext(defaultDispatcher){recipeLocalDataSource.getRandomRecipes().map {
                    recipeEntity ->  recipeEntity.toRecipe()
                }
            }
            OhuneloResult.Success(recipes)
        }
         catch (e: HttpException){
             val recipes = withContext(defaultDispatcher){recipeLocalDataSource.getRandomRecipes().map {
                     recipeEntity ->  recipeEntity.toRecipe()
                }
             }
             Timber.e("HTTPError: $e, ErrorMessage: ${e.message()}")
             OhuneloResult.Error(UiText.StringResource(R.string.failed_to_get_recipes), data = recipes)
         }
        catch (e: Exception){
            val recipes = withContext(defaultDispatcher){recipeLocalDataSource.getRandomRecipes().map {
                    recipeEntity ->  recipeEntity.toRecipe()
                }
            }
            Timber.e("GeneralError: $e, ErrorMessage: ${e.message}")
            OhuneloResult.Error(UiText.StringResource(R.string.failed_to_get_recipes), data = recipes)
        }
    }

    override suspend fun getRecipesByMealType(
        mealType: String
    ): OhuneloResult<List<Recipe>> {
        return try {
            // Get the recipes from api
            val result = recipeNetworkDataSource.getRecipesByMealType(mealType)
            // Convert recipes response to recipe entities
            val recipeEntities = withContext(defaultDispatcher){
                result.results.map {
                        recipeResponse ->
                    recipeResponse.toRecipeEntity()
                }
            }
            // Insert recipe entities into database
            recipeLocalDataSource.insertRecipes(recipeEntities)
            // Get the recipe entities from the database and convert to recipes
            val recipes = withContext(defaultDispatcher){recipeLocalDataSource.getRecipesByMealType(mealType).map {
                    recipeEntity ->  recipeEntity.toRecipe()
                }
            }
            OhuneloResult.Success(recipes)
        }
        catch (e: HttpException){
            val recipes = withContext(defaultDispatcher){recipeLocalDataSource.getRecipesByMealType(mealType).map {
                    recipeEntity ->  recipeEntity.toRecipe()
                }
            }
            Timber.e("HTTPError: $e, ErrorMessage: ${e.message()}")
            OhuneloResult.Error(UiText.StringResource(R.string.failed_to_get_recipes), data = recipes)
        }

        catch (e: Exception){
            val recipes = withContext(defaultDispatcher){recipeLocalDataSource.getRecipesByMealType(mealType).map {
                    recipeEntity ->  recipeEntity.toRecipe()
                }
            }
            Timber.e("GeneralError: $e, ErrorMessage: ${e.message}")
            OhuneloResult.Error(UiText.StringResource(R.string.failed_to_get_recipes), data = recipes)
        }
    }

    override fun getPagedRecipes(mealType: String): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = RECIPE_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { RecipePagingSource( ioDispatcher, defaultDispatcher, recipeNetworkDataSource, mealType) }
        ).flow
    }
}