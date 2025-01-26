package com.jesse.ohunelo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.RecipePagingSource
import com.jesse.ohunelo.data.local.database.RecipeDao
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.data_source.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.domain.repository.RecipeRepository
import com.jesse.ohunelo.util.HOME_SCREEN_RECIPES_AMOUNT
import com.jesse.ohunelo.util.NetworkErrorException
import com.jesse.ohunelo.util.NotFoundException
import com.jesse.ohunelo.util.RateLimitExceededException
import com.jesse.ohunelo.util.ServerErrorException
import com.jesse.ohunelo.util.UiText
import com.jesse.ohunelo.util.UnauthorizedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    private val recipeDao: RecipeDao,

    @DefaultDispatcher
    private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
): RecipeRepository {
    override suspend fun getRandomRecipes(): OhuneloResult<List<Recipe>> {
         return withContext(ioDispatcher){
             try {
                 // Get the recipes from api
                 val result = recipeNetworkDataSource.getRecipes(sort = "random");
                 // Convert recipes response to recipe entities
                 val recipeEntities = withContext(defaultDispatcher){
                     result.results.map {
                             recipeResponse ->
                         val recipeEnt = recipeResponse.toRecipeEntity()
                         Timber.e("Recipe Calories2: ${recipeEnt.nutritionEntity?.calories}")
                         recipeEnt
                     }
                 }

                 // Insert recipe entities into database
                 recipeDao.insertRecipes(recipeEntities)
                 // Get the recipe entities from the database and convert to recipes
                 val recipes = withContext(defaultDispatcher){recipeDao.getRandomRecipes().map {
                         recipeEntity ->
                     Timber.e("Recipe Calories2.1: ${recipeEntity.nutritionEntity?.calories}")
                     recipeEntity.toRecipe()
                    }
                 }
                 Timber.e("Recipe Calories3: ${recipes[0].nutrition?.calories}")
                 Timber.e("Local Recipe from Repo: ${recipes.size}")
                 OhuneloResult.Success(recipes)
             }
             catch (e: IOException){
                 val recipes = withContext(defaultDispatcher){recipeDao.getRandomRecipes().map {
                         recipeEntity ->  recipeEntity.toRecipe()
                    }
                 }
                 Timber.e("IOException: $e")
                 OhuneloResult.Error(error = NetworkErrorException(), data = recipes)
             }
             catch (e: HttpException){
                 val recipes = withContext(defaultDispatcher){recipeDao.getRandomRecipes().map {
                         recipeEntity ->  recipeEntity.toRecipe()
                    }
                 }
                 Timber.e("ErrorMessage: ${e.message()}")
                 when(e.code()){
                    401 -> OhuneloResult.Error(error = UnauthorizedException(), data = recipes)
                    402 -> OhuneloResult.Error(error = RateLimitExceededException(), data = recipes)
                    404 -> OhuneloResult.Error(error = NotFoundException(), data = recipes)
                    500 -> OhuneloResult.Error(error = ServerErrorException(), data = recipes)
                    else -> OhuneloResult.Error(error = e, data = recipes)
                 }
             }
             catch (e: Exception){
                 val recipes = withContext(defaultDispatcher){recipeDao.getRandomRecipes().map {
                         recipeEntity ->  recipeEntity.toRecipe()
                    }
                 }
                 Timber.e("GeneralError: $e, ErrorMessage: ${e.message}")
                 UiText.StringResource(R.string.failed_to_get_recipes)
                 OhuneloResult.Error(error = e, data = recipes)
             }
         }
    }

    override suspend fun getRecipesByMealType(
        mealType: String
    ): OhuneloResult<List<Recipe>> {
        return withContext(ioDispatcher){
            try {
                // Get the recipes from api
                val result = recipeNetworkDataSource.getRecipes(mealType = mealType)
                // Convert recipes response to recipe entities
                val recipeEntities = withContext(defaultDispatcher){
                    result.results.map {
                            recipeResponse ->
                        recipeResponse.toRecipeEntity()
                    }
                }
                // Insert recipe entities into database
                recipeDao.insertRecipes(recipeEntities)
                // Get the recipe entities from the database and convert to recipes
                val recipes = withContext(defaultDispatcher){recipeDao.getAllRecipes().filter {
                    it.dishTypes.contains(mealType)
                }.shuffled().take(HOME_SCREEN_RECIPES_AMOUNT).map {
                        recipeEntity ->  recipeEntity.toRecipe()
                    }
                }
                OhuneloResult.Success(recipes)
            }
            catch (e: IOException){
                val recipes = withContext(defaultDispatcher){recipeDao.getAllRecipes().filter {
                    it.dishTypes.contains(mealType)
                }.shuffled().take(HOME_SCREEN_RECIPES_AMOUNT).map {
                        recipeEntity ->  recipeEntity.toRecipe()
                    }
                }
                Timber.e("IOException: $e")
                UiText.StringResource(R.string.network_error_occured)
                OhuneloResult.Error(error = NetworkErrorException(), data = recipes)
            }
            catch (e: HttpException){
                val recipes = withContext(defaultDispatcher){recipeDao.getAllRecipes().filter {
                    it.dishTypes.contains(mealType)
                }.shuffled().take(HOME_SCREEN_RECIPES_AMOUNT).map {
                        recipeEntity ->  recipeEntity.toRecipe()
                    }
                }
                Timber.e("ErrorMessage: ${e.message()}")
                when(e.code()){
                    401 -> OhuneloResult.Error(error = UnauthorizedException(), data = recipes)
                    402 -> OhuneloResult.Error(error = RateLimitExceededException(), data = recipes)
                    404 -> OhuneloResult.Error(error = NotFoundException(), data = recipes)
                    500 -> OhuneloResult.Error(error = ServerErrorException(), data = recipes)
                    else -> OhuneloResult.Error(error = e, data = recipes)
                }
            }
            catch (e: Exception){
                val recipes = withContext(defaultDispatcher){recipeDao.getAllRecipes().filter {
                    it.dishTypes.contains(mealType)
                }.shuffled().take(HOME_SCREEN_RECIPES_AMOUNT).map {
                        recipeEntity ->  recipeEntity.toRecipe()
                    }
                }
                Timber.e("GeneralRecipeError: $e, ErrorMessage: ${e.message}")
                OhuneloResult.Error(error = e, data = recipes)
            }
        }
    }

    override fun getPagedRecipes(mealType: String, sort: String, searchQuery: String): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(pageSize = 70, enablePlaceholders = false),
            pagingSourceFactory = { RecipePagingSource( ioDispatcher, defaultDispatcher, recipeNetworkDataSource, mealType, sort, searchQuery) }
        ).flow
    }
}