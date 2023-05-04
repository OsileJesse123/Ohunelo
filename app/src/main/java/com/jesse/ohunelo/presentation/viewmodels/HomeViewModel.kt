package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.data.network.models.*
import com.jesse.ohunelo.presentation.uistates.HomeUiState
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _homeUiStateFlow: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiStateFlow: StateFlow<HomeUiState> get() = _homeUiStateFlow.asStateFlow()

    // todo: this acts a source of recipe data for now and would be removed soon
   private var recipes = listOf<Recipe>()

    // todo: when data layer is properly setup changes will be made here
    init {

        /*
        * "name": "",
                    "steps": [
                        {
                            "number": 1,
                            "step": "Place bread cubes in 13- by 9- by 2-inch baking pan.",
                            "ingredients": [
                                {
                                    "id": 10018064,
                                    "name": "bread cubes",
                                    "localizedName": "bread cubes",
                                    "image": "croutons.png"
                                }
                            ],
                            "equipment": [
                                {
                                    "id": 404646,
                                    "name": "baking pan",
                                    "localizedName": "baking pan",
                                    "image": "roasting-pan.jpg"
                                }
                            ]
                        },
                        {
                            "number": 2,
                            "step": "Whisk eggs, whipping cream, milk, sugar, Frangelico, vanilla extract and almond extract in large bowl to blend.",
                            "ingredients": [
                                {
                                    "id": 2050,
                                    "name": "vanilla extract",
                                    "localizedName": "vanilla extract",
                                    "image": "vanilla-extract.jpg"
                                },
                                {
                                    "id": 1002050,
                                    "name": "almond extract",
                                    "localizedName": "almond extract",
                                    "image": "extract.png"
                                },
                                {
                                    "id": 1001053,
                                    "name": "whipping cream",
                                    "localizedName": "whipping cream",
                                    "image": "fluid-cream.jpg"
                                },
                                {
                                    "id": 0,
                                    "name": "frangelico",
                                    "localizedName": "frangelico",
                                    "image": "hazelnut-liqueur-with-nuts.png"
                                },
                                {
                                    "id": 19335,
                                    "name": "sugar",
                                    "localizedName": "sugar",
                                    "image": "sugar-in-bowl.png"
                                },
                                {
                                    "id": 1123,
                                    "name": "egg",
                                    "localizedName": "egg",
                                    "image": "egg.png"
                                },
                                {
                                    "id": 1077,
                                    "name": "milk",
                                    "localizedName": "milk",
                                    "image": "milk.png"
                                }
                            ],
                            "equipment": [
                                {
                                    "id": 404661,
                                    "name": "whisk",
                                    "localizedName": "whisk",
                                    "image": "whisk.png"
                                },
                                {
                                    "id": 404783,
                                    "name": "bowl",
                                    "localizedName": "bowl",
                                    "image": "bowl.jpg"
                                }
                            ]
                        },
                        {
                            "number": 3,
                            "step": "Pour over bread cubes.",
                            "ingredients": [
                                {
                                    "id": 10018064,
                                    "name": "bread cubes",
                                    "localizedName": "bread cubes",
                                    "image": "croutons.png"
                                }
                            ],
                            "equipment": []
                        },
                        {
                            "number": 4,
                            "step": "Let stand 30 minutes, occasionally pressing bread into custard mixture. (Can be prepared 2 hours ahead. Cover and refrigerate.)Preheat oven to 350 degrees. Arrange reserved bread crusts on baking sheet and bake until dry, about 10 minutes. Cool. Maintain oven temperature.",
                            "ingredients": [
                                {
                                    "id": 19170,
                                    "name": "custard",
                                    "localizedName": "custard",
                                    "image": "custard.png"
                                },
                                {
                                    "id": 18064,
                                    "name": "bread",
                                    "localizedName": "bread",
                                    "image": "white-bread.jpg"
                                }
                            ],
                            "equipment": [
                                {
                                    "id": 404727,
                                    "name": "baking sheet",
                                    "localizedName": "baking sheet",
                                    "image": "baking-sheet.jpg"
                                },
                                {
                                    "id": 404784,
                                    "name": "oven",
                                    "localizedName": "oven",
                                    "image": "oven.jpg"
                                }
                            ],
                            "length": {
                                "number": 160,
                                "unit": "minutes"
                            }
                        },
                        {
                            "number": 5,
                            "step": "Transfer crusts to food processor and grind until fine crumbs form.",
                            "ingredients": [],
                            "equipment": [
                                {
                                    "id": 404771,
                                    "name": "food processor",
                                    "localizedName": "food processor",
                                    "image": "food-processor.png"
                                }
                            ]
                        },
                        {
                            "number": 6,
                            "step": "Sprinkle 1 cup crust crumbs over top of pudding.",
                            "ingredients": [
                                {
                                    "id": 0,
                                    "name": "crust",
                                    "localizedName": "crust",
                                    "image": ""
                                }
                            ],
                            "equipment": []
                        },
                        {
                            "number": 7,
                            "step": "Bake until pudding is set in center, about 40 minutes. Cool slightly.",
                            "ingredients": [],
                            "equipment": [
                                {
                                    "id": 404784,
                                    "name": "oven",
                                    "localizedName": "oven",
                                    "image": "oven.jpg"
                                }
                            ],
                            "length": {
                                "number": 40,
                                "unit": "minutes"
                            }
                        },
                        {
                            "number": 8,
                            "step": "Serve warm.This recipe yields 8 to 10 servings.",
                            "ingredients": [],
                            "equipment": []
                        }
                    ]
                }
        * */
        updateGreeting()
        recipes =  listOf(
            Recipe(
                id = 1,
                analyzedInstructions = listOf(
                    AnalyzedInstructions(name = "Tortilla Chip", steps = listOf(Step(number = 1, step = "Preheat oven to 350", ingredients = listOf(), equipment = listOf()), Step(number = 2, step = "In a large bowl beat the butter with an electric mixer on medium speed for 30 seconds.", ingredients = listOf(), equipment = listOf()), Step(number = 1, step = "Add brown sugar, maple syrup, baking soda, cinnamon, ginger and salt. Beat until combined.", ingredients = listOf(), equipment = listOf()), Step(number = 1, step = "Beat in egg, applesauce and vanilla. Beat in as much flour as you can with mixer. Stir in remaining flour, carrots, raisins, walnuts just until combined.", ingredients = listOf(), equipment = listOf()))),
                    AnalyzedInstructions(name = "", steps = listOf(Step(number = 1, step = "Preheat oven to 350", ingredients = listOf(), equipment = listOf()))),
                ),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")), image = "olive-oil.jpg"
                ), ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")), image = "olive-oil.jpg"
                ), ExtendedIngredient(id = 1034053,
                    aisle = "Oil, Vinegar, Salad Dressing", consistency = "LIQUID",
                    name = "extra virgin olive oil", nameClean = "extra virgin olive oil",
                    original = "1-2 tbsp extra virgin olive oil", originalName = "extra virgin olive oil",
                    amount = 1.0, unit = "tbsp", meta = listOf(),
                    measures = Measures(us = Us(amount = 1.0, unitLong = "Tbsp", unitShort = "Tbsp"),
                        metric = Metric(amount = 1.0, unitShort = "Tbsp", unitLong = "Tbsp")), image = "olive-oil.jpg"
                )),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            Recipe(
                id = 1,
                analyzedInstructions = listOf(),
                cookingMinutes = 20,
                creditsText = "creditsText",
                extendedIngredients = listOf(),
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
                caloricBreakdown = CaloricBreakdown(20.0, 30.0, 15.0),
                summary = "This is a very long text that needs to be truncated. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
        )
        _homeUiStateFlow.update {
            state ->
            state.copy(randomRecipes = recipes, recipesByCategory = recipes)
        }
    }

    fun updateGreeting(){
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        when(currentHour){
            in 0..11 -> {_homeUiStateFlow.update { it.copy(userGreetingText = UiText.StringResource(
                R.string.good_morning), userGreetingIcon = UiDrawable(R.drawable.sun_icon)
            ) }}
            in 12..16 -> {_homeUiStateFlow.update { it.copy(userGreetingText =
            UiText.StringResource(R.string.good_afternoon),
                userGreetingIcon = UiDrawable(R.drawable.sun_icon)
            ) }}
            in 17..21 -> {_homeUiStateFlow.update { it.copy(userGreetingText =
            UiText.StringResource(R.string.good_evening),
                userGreetingIcon = UiDrawable(R.drawable.moon_icon)
            ) }}
            else -> {_homeUiStateFlow.update { it.copy(userGreetingText =
            UiText.StringResource(R.string.good_evening),
                userGreetingIcon = UiDrawable(R.drawable.moon_icon)) }}
        }

    }
}