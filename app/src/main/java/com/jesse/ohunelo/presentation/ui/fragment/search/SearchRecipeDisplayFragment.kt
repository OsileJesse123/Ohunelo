package com.jesse.ohunelo.presentation.ui.fragment.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.SearchRecipeDisplayAdapter
import com.jesse.ohunelo.adapters.SeeAllRecipesLoadStateAdapter
import com.jesse.ohunelo.databinding.FragmentSearchRecipeDisplayBinding
import com.jesse.ohunelo.presentation.viewmodels.SearchRecipeDisplayViewModel
import com.jesse.ohunelo.util.GridSpacingItemDecoration
import com.jesse.ohunelo.util.UiTextThrowable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class SearchRecipeDisplayFragment : Fragment() {

    private var _binding: FragmentSearchRecipeDisplayBinding? = null
    private val binding: FragmentSearchRecipeDisplayBinding get() = _binding!!

    private val viewModel by viewModels<SearchRecipeDisplayViewModel>()

    private var _searchRecipeDisplayAdapter: SearchRecipeDisplayAdapter? = null
    private val searchRecipeDisplayAdapter: SearchRecipeDisplayAdapter get() = _searchRecipeDisplayAdapter!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_recipe_display,
            container, false)

        _searchRecipeDisplayAdapter = SearchRecipeDisplayAdapter{
            recipe ->
            val action = SearchRecipeDisplayFragmentDirections
                .actionSearchRecipeDisplayFragmentToRecipeDetailsFragment(recipe = recipe, recipeId = recipe.id)
            findNavController().navigate(action)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This has to be called first so that searchDisplayEditText can request focus
        setupListeners()

        // Show the soft keyboard
        showSoftKeyboard(binding.searchDisplayEditText)


        setupRecycler()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.recipes.collectLatest {
                    searchRecipeDisplayAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                searchRecipeDisplayAdapter.loadStateFlow.collectLatest {
                        combinedLoadStates ->

                    val loadStateRefresh = combinedLoadStates.refresh

                    if (loadStateRefresh is LoadState.Error){
                        val errorMessage = if(loadStateRefresh.error is UiTextThrowable) (loadStateRefresh.error as UiTextThrowable).errorMessage.asString(requireContext()) else loadStateRefresh.error.localizedMessage
                        binding.errorMessageText.text = errorMessage
                    }
                    binding.errorLayout.isVisible = loadStateRefresh is LoadState.Error

                    binding.searchRecipeDisplayShimmer.isVisible = loadStateRefresh is LoadState.Loading

                    binding.searchDisplayRecycler.isVisible = loadStateRefresh is LoadState.NotLoading

                    binding.emptyListText.isVisible = searchRecipeDisplayAdapter.itemCount == 0 && loadStateRefresh is LoadState.NotLoading
                }
            }
        }
    }

    private fun setupListeners(){
        binding.apply {

            searchDisplayEditText.apply {
                // Request focus for edit text
                requestFocus()
                setOnEditorActionListener { _, actionId, _ ->
                    if(actionId == EditorInfo.IME_ACTION_SEARCH){
                        hideSoftKeyboard()
                        viewModel.searchRecipes(this.text.toString())
                        true
                    } else{
                        false
                    }
                }
            }
            navigationButton.setOnClickListener {
                findNavController().navigateUp()
            }
            tryAgainButton.setOnClickListener {
                searchRecipeDisplayAdapter.retry()
            }
        }
    }

    private fun setupRecycler(){
        binding.searchDisplayRecycler.apply {
            val spacing = resources.getDimensionPixelSize(R.dimen.grid_1)
            adapter = searchRecipeDisplayAdapter.withLoadStateFooter(SeeAllRecipesLoadStateAdapter{
                searchRecipeDisplayAdapter.retry()
            })
            layoutManager = GridLayoutManager(requireContext(), 1).apply {
                addItemDecoration(GridSpacingItemDecoration(1,spacing,false))
            }
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideSoftKeyboard(){
        requireActivity().apply {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            currentFocus?.let {
                view ->
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _searchRecipeDisplayAdapter = null
        _binding = null
    }
}