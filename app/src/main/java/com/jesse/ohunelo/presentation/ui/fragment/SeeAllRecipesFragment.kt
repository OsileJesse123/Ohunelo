package com.jesse.ohunelo.presentation.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.SeeAllRecipesAdapter
import com.jesse.ohunelo.adapters.SeeAllRecipesLoadStateAdapter
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.FragmentSeeAllRecipesBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.RecipeExpandedItemDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.SeeAllRecipesViewModel
import com.jesse.ohunelo.util.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeeAllRecipesFragment : Fragment() {

    private var _binding: FragmentSeeAllRecipesBinding? = null
    private val binding: FragmentSeeAllRecipesBinding get() = _binding!!

    private var _seeAllRecipesAdapter: SeeAllRecipesAdapter? = null
    private val seeAllRecipesAdapter get() = _seeAllRecipesAdapter!!

    private val viewModel by viewModels<SeeAllRecipesViewModel>()

    private val args by navArgs<SeeAllRecipesFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_see_all_recipes,
            container, false)

        _seeAllRecipesAdapter = SeeAllRecipesAdapter {
            recipe ->
            showRecipeDetailsInDialog(recipe)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setupRecycler()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.recipes.collectLatest {
                    seeAllRecipesAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                seeAllRecipesAdapter.loadStateFlow.collectLatest {
                    combinedLoadStates ->

                    val loadStateRefresh = combinedLoadStates.refresh

                    if (loadStateRefresh is LoadState.Error){
                        binding.errorLayout.isVisible = true
                        binding.errorMessageText.text = loadStateRefresh.error.localizedMessage
                    }

                    binding.seeAllRecipesShimmer.isVisible = loadStateRefresh is LoadState.Loading
                }
            }
        }
    }

    private fun setupRecycler(){
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_2)
        val spanCount = 3
        binding.seeAllRecycler.apply {
            adapter = seeAllRecipesAdapter.withLoadStateFooter(SeeAllRecipesLoadStateAdapter{
                seeAllRecipesAdapter.retry()
            })
            layoutManager = GridLayoutManager(requireContext(), spanCount).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return when(seeAllRecipesAdapter.getItemViewType(position)){
                            R.layout.see_all_recipes_load_state_footer_item ->{
                                spanCount
                            }
                            else -> 1
                        }
                    }
                }
            }
            addItemDecoration(GridSpacingItemDecoration(spanCount = 3, spacing, includeEdge = true))
        }
    }

    private fun setupToolbar(){
        binding.allRecipesToolBar.apply {
            title = args.recipeCategory
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }


    }

    private fun showRecipeDetailsInDialog(selectedRecipe: Recipe){
        RecipeExpandedItemDialogFragment(selectedRecipe){
            recipe ->
            // Navigate to Recipe Details screen
            findNavController().navigate(SeeAllRecipesFragmentDirections
                .actionSeeAllRecipesFragmentToRecipeDetailsFragment(recipe = recipe,
                    recipeId = recipe.id))
        }
            .show(childFragmentManager, RecipeExpandedItemDialogFragment.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _seeAllRecipesAdapter = null
        _binding = null
    }
}