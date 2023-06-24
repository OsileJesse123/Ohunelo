package com.jesse.ohunelo.presentation.ui.fragment.recipe_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.IngredientAdapter
import com.jesse.ohunelo.data.network.models.ExtendedIngredient
import com.jesse.ohunelo.databinding.FragmentIngredientsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IngredientsFragment(private val ingredients: List<ExtendedIngredient> = listOf()) : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null
    private val binding: FragmentIngredientsBinding get() = _binding!!

    private var _ingredientAdapter: IngredientAdapter? = null
    private val ingredientAdapter: IngredientAdapter get() = _ingredientAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemCount.text = resources.getQuantityString(R.plurals.number_of_items_available, ingredients.size, ingredients.size)

        determineWhatToDisplay()

    }

    private fun determineWhatToDisplay(){
        if(ingredients.isEmpty()){
            // If ingredients comes in as empty, display the no ingredients text
            binding.noIngredientsText.isVisible = true
        } else {
            // Else setup the recycler and display the items on the screen
            setupRecycler()
        }
    }

    private fun setupRecycler(){
        _ingredientAdapter = IngredientAdapter()

        binding.ingredientsRecycler.apply {
            adapter = ingredientAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        ingredientAdapter.submitList(ingredients)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ingredientAdapter = null
        _binding = null
    }
}