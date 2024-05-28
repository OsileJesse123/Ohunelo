package com.jesse.ohunelo.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.data_source.RecipeNetworkDataSource
import com.jesse.ohunelo.util.NotFoundException
import com.jesse.ohunelo.util.RateLimitExceededException
import com.jesse.ohunelo.util.ServerErrorException
import com.jesse.ohunelo.util.UnauthorizedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

private const val STARTING_KEY = 0
private const val RECIPE_PAGE_SIZE = 39

class RecipePagingSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher,
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    private val mealType: String = "",
    private val sort: String = "",
    private val searchQuery: String = ""
): PagingSource<Int, Recipe>() {
    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {

        return try {
            val startKey = params.key ?: STARTING_KEY
            val offset = startKey * RECIPE_PAGE_SIZE
            val recipes = withContext(ioDispatcher){recipeNetworkDataSource.getRecipes(mealType = mealType, offset = offset, number = RECIPE_PAGE_SIZE, sort = sort, searchQuery = searchQuery)}
            val numberOfPages = if(recipes.totalResults % RECIPE_PAGE_SIZE == 0) recipes.totalResults/RECIPE_PAGE_SIZE else (recipes.totalResults/RECIPE_PAGE_SIZE) + 1
            val nextKey =  if(startKey < numberOfPages) startKey + 1 else null
            val prevKey = if(startKey < 1) null else startKey - 1
            LoadResult.Page(
                data = withContext(defaultDispatcher){
                    recipes.results.map {
                        recipeResponse ->
                        recipeResponse.toRecipe()
                    }
                },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: HttpException){
            Timber.e("ErrorMessage: ${e.message}")
            when(e.code()){
                401 -> LoadResult.Error(UnauthorizedException())
                402 -> LoadResult.Error(RateLimitExceededException())
                404 -> LoadResult.Error(NotFoundException())
                500 -> LoadResult.Error(ServerErrorException())
                else -> LoadResult.Error(e)
            }
        } catch (e: Exception){
            Timber.e("Error: $e")
            LoadResult.Error(e)
        }
    }
}