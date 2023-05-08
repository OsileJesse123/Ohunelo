package com.jesse.ohunelo.presentation.ui.fragment.recipe_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.ViewPagerAdapter
import com.jesse.ohunelo.databinding.FragmentRecipeDetailsBinding

class RecipeDetailsFragment : Fragment() {

    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding: FragmentRecipeDetailsBinding get() = _binding!!

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private lateinit var bottomSheetCallback: BottomSheetCallback

    private val args by navArgs<RecipeDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)

        binding.apply {
            recipe = args.recipe
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        setupBottomSheet()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()

        setupViewPager()

    }

    private fun setupOnClickListeners(){
        binding.apply {

            recipeDetailsToolbar.setNavigationOnClickListener {
                // Reset the bottom sheet to it's initial state which was collapsed
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupBottomSheet(){

        bottomSheetBehavior = BottomSheetBehavior.from(binding.recipeDetailsBottomSheet)

        bottomSheetCallback = object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> { binding.recipeDetailsToolbar.visibility = View.VISIBLE }
                    else -> { binding.recipeDetailsToolbar.visibility = View.GONE }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        }

        bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun setupViewPager(){
        // todo: ensure to add other fragments when they are added
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle,
            listOf(
                IngredientsFragment(args.recipe.extendedIngredients),
                InstructionsFragment(args.recipe.analyzedInstructions){
                   analyzedInstruction ->
                    val action = RecipeDetailsFragmentDirections
                        .actionRecipeDetailsFragmentToStepsFragment(analyzedInstruction)
                   findNavController().navigate(action)
                }
            ))

        binding.recipeDetailsViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.recipeDetailsTabLayout, binding.recipeDetailsViewPager){
            tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.ingredients)
                1 -> tab.text = getString(R.string.instructions)
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior = null
        _binding = null
    }
}
