package com.jesse.ohunelo.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jesse.ohunelo.data.model.Nutrition
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.data.network.models.Measures
import com.jesse.ohunelo.data.network.models.Metric
import com.jesse.ohunelo.data.network.models.Step
import com.jesse.ohunelo.data.network.models.Us
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val STARTING_KEY = 0
// LOAD_DELAY_MILLIS will be removed as soon as backend is ready
private const val LOAD_DELAY_MILLIS = 1_000L

// This is an implementation for UI development, this will change as soon as back end
// is plugged in
class RecipePagingSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher,
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    private val sort: String
): PagingSource<Int, Recipe>() {
    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {

        try {
            val startKey = params.key ?: STARTING_KEY
            val recipes = withContext(ioDispatcher){recipeNetworkDataSource.getRecipes(sort = sort, offset = (startKey * 40))}
            val numberOfPages = recipes.totalResults
            val nextKey = startKey + 1
            val prevKey = startKey - 1

            /*LoadResult.Page(
                data = withContext(defaultDispatcher){
                    recipes.results
                },
                prevKey = when(start){
                    STARTING_KEY -> null
                    else -> ensureValidKey(key = range.first - params.loadSize)
                },
                nextKey = if(range.last + 1 >= 100) null else range.last + 1
            )*/
        } catch (e: Exception){

        }
        // Start paging with STARTING_KEY if this is the first load
        val start = params.key ?: STARTING_KEY

        // Load as many items as hinted by params.loadSize
        val range = (0..100)

        if (start != STARTING_KEY) delay(LOAD_DELAY_MILLIS) else delay(2000)

        return try{
            LoadResult.Page(
                data = withContext(defaultDispatcher){
                    range.map {
        number ->
        Recipe(
            id = number,
            analyzedInstructions = listOf(
                AnalyzedInstructions(name = "Tortilla Chip", steps = listOf(Step(number = 1, step = "Preheat oven to 350", ingredients = listOf(), equipment = listOf()), Step(number = 2, step = "In a large bowl beat the butter with an electric mixer on medium speed for 30 seconds.", ingredients = listOf(), equipment = listOf()), Step(number = 3, step = "Add brown sugar, maple syrup, baking soda, cinnamon, ginger and salt. Beat until combined.", ingredients = listOf(), equipment = listOf()), Step(number = 4, step = "Beat in egg, applesauce and vanilla. Beat in as much flour as you can with mixer. Stir in remaining flour, carrots, raisins, walnuts just until combined.", ingredients = listOf(), equipment = listOf()))),
                AnalyzedInstructions(name = "", steps = listOf(Step(number = 1, step = "Preheat oven to 350", ingredients = listOf(), equipment = listOf()))),
            ),
            cookingMinutes = 20,
            creditsText = "creditsText",
            extendedIngredients = listOf(
                ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")
                    ), image = "olive-oil.jpg"
                ), ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")
                    ), image = "olive-oil.jpg"
                ), ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")
                    ), image = "olive-oil.jpg"
                )
            ),
            healthScore = 5,
            image = "image",
            imageType = "imageType",
            instructions = "instructions",
            preparationMinutes = 20,
            pricePerServing = 100.00,
            readyInMinutes = 20,
            servings = 2,
            sourceName = "Anthony Joshua",
            title = "Asian Chickpea Lettuce Wraps",
            weightWatcherSmartPoints = 33,
            summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            nutrition = Nutrition(1, calories = "316", carbs = "49g", fat = "12g", protein = "3g")
        )
    }
                },
                prevKey = when(start){
                    STARTING_KEY -> null
                    else -> ensureValidKey(key = range.first - params.loadSize)
                },
                nextKey = if(range.last + 1 >= 100) null else range.last + 1
            )
        } catch (exc: Exception){
            LoadResult.Error(exc)
        }
    }

    /*range.map {
        number ->
        Recipe(
            id = number,
            analyzedInstructions = listOf(
                AnalyzedInstructions(name = "Tortilla Chip", steps = listOf(Step(number = 1, step = "Preheat oven to 350", ingredients = listOf(), equipment = listOf()), Step(number = 2, step = "In a large bowl beat the butter with an electric mixer on medium speed for 30 seconds.", ingredients = listOf(), equipment = listOf()), Step(number = 3, step = "Add brown sugar, maple syrup, baking soda, cinnamon, ginger and salt. Beat until combined.", ingredients = listOf(), equipment = listOf()), Step(number = 4, step = "Beat in egg, applesauce and vanilla. Beat in as much flour as you can with mixer. Stir in remaining flour, carrots, raisins, walnuts just until combined.", ingredients = listOf(), equipment = listOf()))),
                AnalyzedInstructions(name = "", steps = listOf(Step(number = 1, step = "Preheat oven to 350", ingredients = listOf(), equipment = listOf()))),
            ),
            cookingMinutes = 20,
            creditsText = "creditsText",
            extendedIngredients = listOf(
                ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")
                    ), image = "olive-oil.jpg"
                ), ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")
                    ), image = "olive-oil.jpg"
                ), ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")
                    ), image = "olive-oil.jpg"
                )
            ),
            healthScore = 5,
            image = "image",
            imageType = "imageType",
            instructions = "instructions",
            preparationMinutes = 20,
            pricePerServing = 100.00,
            readyInMinutes = 20,
            servings = 2,
            sourceName = "Anthony Joshua",
            title = "Asian Chickpea Lettuce Wraps",
            weightWatcherSmartPoints = 33,
            summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            nutrition = Nutrition(1, calories = "316", carbs = "49g", fat = "12g", protein = "3g")
        )
    }*/

    private fun ensureValidKey(key: Int) = Integer.max(STARTING_KEY, key)
}