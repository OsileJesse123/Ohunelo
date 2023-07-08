package com.jesse.ohunelo.presentation.ui.fragment

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.SearchRecipeAdapter
import com.jesse.ohunelo.adapters.SeeAllRecipesLoadStateAdapter
import com.jesse.ohunelo.databinding.FragmentSearchRecipeBinding
import com.jesse.ohunelo.presentation.viewmodels.SearchRecipeViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_recipe, container,
            false)

        _searchRecipeAdapter = SearchRecipeAdapter()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.recipes.collectLatest {
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
                        binding.errorLayout.isVisible = true
                        binding.errorMessageText.text = loadStateRefresh.error.localizedMessage
                    }

                    binding.loadingProgressBar.isVisible = loadStateRefresh is LoadState.Loading
                }
            }
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
                    if (position % 6 == 0 || position % 6 == 4) {
                        SpanSize(2, 2)
                    }else if (searchRecipeAdapter.getItemViewType(position) == R.layout.see_all_recipes_load_state_footer_item){
                        SpanSize(3, 1)
                    }
                    else {
                        SpanSize(1, 1)
                    }

                }
            }
            addItemDecoration(SpaceItemDecorator(spacing))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}