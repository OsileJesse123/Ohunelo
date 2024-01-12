package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jesse.ohunelo.data.model.Nutrition
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.data.network.models.Measures
import com.jesse.ohunelo.data.network.models.Metric
import com.jesse.ohunelo.data.network.models.Step
import com.jesse.ohunelo.data.network.models.Us
import com.jesse.ohunelo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchRecipeDisplayViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    val recipes = recipeRepository.getPagedRecipes().cachedIn(viewModelScope)

    val recis: MutableStateFlow<List<Recipe>> = MutableStateFlow(listOf());

    init {
        recis.update {
            (1..20).map {
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
        }
    }
}