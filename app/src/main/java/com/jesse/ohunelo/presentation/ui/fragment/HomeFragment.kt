package com.jesse.ohunelo.presentation.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.RecipeAdapter
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.FragmentHomeBinding
import com.jesse.ohunelo.presentation.viewmodels.HomeViewModel
import com.jesse.ohunelo.util.RecipeViewHolderType
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

    private val homeViewModel by viewModels<HomeViewModel>()

    private val timeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.tag("UserGreeting").e("Greeting about to be updated")
            intent?.let {
                intent ->
                if(intent.action == Intent.ACTION_TIME_TICK){
                    Timber.tag("UserGreeting").e("Greeting has been updated")
                    homeViewModel.updateGreeting()
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

        // Initialize adapters
        randomRecipeAdapter = RecipeAdapter(RecipeViewHolderType.RANDOM_RECIPE){
            navigateToRecipeDetail(it)
        }
        recipeByCategoryAdapter = RecipeAdapter(RecipeViewHolderType.RECIPE_BY_CATEGORY){
            navigateToRecipeDetail(it)
        }

        setupBinding()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerHeaders()

        setupRecyclers()

        setupSwipeToRefreshLayout()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.homeUiStateFlow.collect{
                    homeUiState ->
                    randomRecipeAdapter?.submitList(homeUiState.randomRecipes)
                    recipeByCategoryAdapter?.submitList(homeUiState.recipesByCategory)
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

    private fun setupSwipeToRefreshLayout(){
        binding.swipeToRefreshLayout.apply {
            setOnRefreshListener {
                Timber.e("Layout refreshed")
                // todo: Proper implementation for isRefreshing will be added
                isRefreshing = false
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

    private fun setupBinding(){
        // Set User name and ViewModel
        binding.apply {
            userName = "Osile Jacksonn"
            viewModel = homeViewModel
            executePendingBindings()
        }
    }

    private fun navigateToRecipeDetail(recipe: Recipe){
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToRecipeDetailsFragment(recipe = recipe,
                recipeId = recipe.id)
        )
    }

    private fun setupRecyclerHeaders(){
        binding.randomRecipesRecyclerHeader.title = getString(R.string.random)
        binding.recipesByCategoryRecyclerHeader.title = getString(R.string.recipes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        randomRecipeAdapter = null
        recipeByCategoryAdapter = null
        _binding = null
    }

}