package com.jesse.ohunelo.presentation.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.RecipeAdapter
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.FragmentHomeBinding
import com.jesse.ohunelo.presentation.viewmodels.HomeViewModel
import com.jesse.ohunelo.presentation.viewmodels.SharedViewModel
import com.jesse.ohunelo.util.RecipeViewHolderType
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

// todo: Make sure to delete ohunelo_branding.webp and pasta_image after you are done with app
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private var randomRecipeAdapter: RecipeAdapter? = null
    private var recipeByCategoryAdapter: RecipeAdapter? = null

    private val viewModel by hiltNavGraphViewModels<HomeViewModel>(R.id.main_nav_graph)

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private val timeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.tag("UserGreeting").e("Greeting about to be updated")
            intent?.let {
                intent ->
                if(intent.action == Intent.ACTION_TIME_TICK){
                    Timber.tag("UserGreeting").e("Greeting has been updated")
                    viewModel.updateGreeting()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container,
            false)

        binding.apply {
            viewModel = this@HomeFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        // Initialize adapters
        randomRecipeAdapter = RecipeAdapter(RecipeViewHolderType.RANDOM_RECIPE){
            navigateToRecipeDetail(it)
        }
        recipeByCategoryAdapter = RecipeAdapter(RecipeViewHolderType.RECIPE_BY_CATEGORY){
            navigateToRecipeDetail(it)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclers()

        setupSwipeToRefreshLayout()


        // Set See All TextView onClickListener
        binding.seeAllTextView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllRecipesFragment(viewModel.selectedRecipeCategory)
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.homeUiStateFlow.collect{
                    homeUiState ->
                    randomRecipeAdapter?.submitList(homeUiState.randomRecipes)
                    recipeByCategoryAdapter?.submitList(homeUiState.recipesByCategory)
                    if(homeUiState.showErrorMessage.first){
                        showErrorMessage(homeUiState.showErrorMessage.second)
                    }
                    if(!homeUiState.shouldKeepSplashScreenOn){
                        sharedViewModel.stopShowingSplashScreen()
                    }
                    // If random recipes and recipes by category are not empty, it means there is data to be displayed so display it
                    binding.homeScreenLayout.isVisible = (homeUiState.randomRecipes.isNotEmpty() && homeUiState.recipesByCategory.isNotEmpty())

                    // If random recipes and recipes by category are empty and screen is no longer loading show the error view
                    binding.foodErrorLayout.isVisible = (homeUiState.randomRecipes.isEmpty() && homeUiState.recipesByCategory.isEmpty() && !homeUiState.loading)

                    // If random recipes is not empty but recipes by category is empty, then show the recipes by category error
                    binding.recipesByCategoryError.isVisible = (homeUiState.randomRecipes.isNotEmpty() && homeUiState.recipesByCategory.isEmpty())

                    // If random recipes, recipes by category is empty and state is loading then show shimmer view.
                    binding.homeShimmer.isVisible = (homeUiState.randomRecipes.isEmpty() && homeUiState.recipesByCategory.isEmpty() && homeUiState.loading)

                    binding.swipeToRefreshLayout.isRefreshing = (homeUiState.randomRecipes.isNotEmpty() && homeUiState.loading)
                    binding.recipesByCategoryRecycler.isVisible = !homeUiState.startShimmer
                    binding.recipesByCategoryShimmer.isVisible = homeUiState.startShimmer
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register a BroadcastReceiver to listen for time changes
        // The Intent.ACTION_TIME_TICK to notifies the app every one minute that time has changed.
        val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
        context?.registerReceiver(timeReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        // timeReceiver is unregistered here to ensure that the broadcast receiver is
        // unregistered before fragment is stopped
        context?.unregisterReceiver(timeReceiver)
    }

    private fun showErrorMessage(errorMessage: UiText?) {
        Toast.makeText(
            requireContext(),
            errorMessage?.asString(requireContext()),
            Toast.LENGTH_LONG
        ).show()
        viewModel.onErrorMessageShown()
    }

    private fun setupSwipeToRefreshLayout(){
        binding.swipeToRefreshLayout.apply {
            setOnRefreshListener {
                viewModel.getRecipesForHomeScreen()
            }
            setColorSchemeResources(R.color.orange_500)
        }
    }

    private fun setupRecyclers(){
        binding.randomRecipesRecycler.apply {
            adapter = randomRecipeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,
                false)
        }
        binding.recipesByCategoryRecycler.apply {
            adapter = recipeByCategoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,
                false)
        }
    }

    private fun navigateToRecipeDetail(recipe: Recipe){
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToRecipeDetailsFragment(recipe = recipe,
                recipeId = recipe.id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        randomRecipeAdapter = null
        recipeByCategoryAdapter = null
        _binding = null
    }

}

// This Interface was created to handle Chip clicks directly from the xml using binding adapters
interface OnRecipeCategorySelectedListener{
    fun onRecipeCategorySelected(selectedRecipeCategory: String)
}