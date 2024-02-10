package com.jesse.ohunelo.presentation.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.SearchRecipeAdapter
import com.jesse.ohunelo.adapters.SeeAllRecipesLoadStateAdapter
import com.jesse.ohunelo.databinding.FragmentSearchRecipeBinding
import com.jesse.ohunelo.presentation.viewmodels.SearchRecipeViewModel
import com.jesse.ohunelo.util.UiTextThrowable
import com.jesse.ohunelo.util.spanned_grid_layout_manager.SpaceItemDecorator
import com.jesse.ohunelo.util.spanned_grid_layout_manager.SpanSize
import com.jesse.ohunelo.util.spanned_grid_layout_manager.SpannedGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchRecipeFragment : Fragment() {

    private var _binding: FragmentSearchRecipeBinding? = null
    private val binding: FragmentSearchRecipeBinding get() = _binding!!

    private var _searchRecipeAdapter: SearchRecipeAdapter? = null
    private val searchRecipeAdapter: SearchRecipeAdapter get() = _searchRecipeAdapter!!

    private val viewModel by viewModels<SearchRecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateRecipes()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_recipe, container,
            false)

        _searchRecipeAdapter = SearchRecipeAdapter{
            recipe ->
            val action = SearchRecipeFragmentDirections
                .actionSearchRecipeFragmentToRecipeDetailsFragment(recipe = recipe, recipeId = recipe.id)
            findNavController().navigate(action)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.recipes?.collectLatest {
                    searchRecipeAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                searchRecipeAdapter.loadStateFlow.collectLatest {
                        combinedLoadStates ->

                    val loadStateRefresh = combinedLoadStates.refresh

                    if (loadStateRefresh is LoadState.Error){
                        val errorMessage = if(loadStateRefresh.error is UiTextThrowable) (loadStateRefresh.error as UiTextThrowable).errorMessage.asString(requireContext()) else loadStateRefresh.error.localizedMessage
                        binding.errorLayout.isVisible = true
                        binding.errorMessageText.text = errorMessage
                    }
                    binding.errorLayout.isVisible = loadStateRefresh is LoadState.Error

                    binding.searchRecipeShimmer.isVisible = loadStateRefresh is LoadState.Loading
                }
            }
        }
        setupOnClickListeners()
    }

    private fun setupOnClickListeners(){
        binding.tryAgainButton.setOnClickListener {
            searchRecipeAdapter.retry()
        }
        binding.searchRecipeToolBar.setOnClickListener {
            findNavController().navigate(SearchRecipeFragmentDirections
                .actionSearchRecipeFragmentToSearchRecipeDisplayFragment())
        }
    }

    private fun setupRecycler(){
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_2)
        binding.seeAllRecycler.apply {
            adapter = searchRecipeAdapter.withLoadStateFooter(SeeAllRecipesLoadStateAdapter{
                searchRecipeAdapter.retry()
            })
            layoutManager = SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL,
                3).apply {
                spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                    when{
                        position % 6 == 0 || position % 6 == 4 -> {
                            SpanSize(2, 2)
                        }
                        position == searchRecipeAdapter.itemCount -> {
                            SpanSize(3, 1)
                        }
                        else -> {
                            SpanSize(1, 1)
                        }
                    }
                }
            }
            addItemDecoration(SpaceItemDecorator(spacing))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _searchRecipeAdapter = null
        _binding = null
    }
}