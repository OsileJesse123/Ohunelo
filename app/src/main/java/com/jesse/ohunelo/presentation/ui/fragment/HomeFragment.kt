package com.jesse.ohunelo.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.RecipeAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container,
            false)

        // Initialize adapters
        randomRecipeAdapter = RecipeAdapter(RecipeViewHolderType.RANDOM_RECIPE){}
        recipeByCategoryAdapter = RecipeAdapter(RecipeViewHolderType.RECIPE_BY_CATEGORY){}

        setupBinding()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclers()

        binding.timeOfDayImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.moon_icon, null))

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

    override fun onDestroyView() {
        super.onDestroyView()
        randomRecipeAdapter = null
        recipeByCategoryAdapter = null
        _binding = null
    }
}