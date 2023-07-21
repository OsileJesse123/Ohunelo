package com.jesse.ohunelo.presentation.ui.fragment.recipe_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.ViewPagerAdapter
import com.jesse.ohunelo.databinding.FragmentRecipeDetailsBinding
import com.jesse.ohunelo.presentation.viewmodels.RecipeDetailsViewModel
import com.jesse.ohunelo.util.BottomSheetBehaviorStateWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailsFragment : Fragment() {

    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding: FragmentRecipeDetailsBinding get() = _binding!!

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private lateinit var bottomSheetCallback: BottomSheetCallback

    private val viewModel: RecipeDetailsViewModel by viewModels()

    private val args by navArgs<RecipeDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_details, container,
            false)

        binding.apply {
            recipe = args.recipe
            fragmentViewMargin = getDimensionPixelSize()
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        setupBottomSheet()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateBottomSheetBehaviorState()

        setupOnClickListeners()

        setupViewPager()

    }


    private fun updateBottomSheetBehaviorState(){
        // Update bottom sheet behavior state
        bottomSheetBehavior?.state = when(viewModel.bottomSheetBehaviorState){
            BottomSheetBehaviorStateWrapper.STATE_COLLAPSED -> {
                binding.recipeDetailsToolbar.visibility = View.GONE
                BottomSheetBehavior.STATE_COLLAPSED
            }
            BottomSheetBehaviorStateWrapper.STATE_EXPANDED -> {
                binding.recipeDetailsToolbar.visibility = View.VISIBLE
                BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun setupOnClickListeners(){
        binding.apply {

            recipeDetailsToolbar.setNavigationOnClickListener {
                // Reset the bottom sheet to it's initial state which was collapsed
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                // Scroll back to top of nested scroll view
                binding.recipeDetailsNestedScroll.smoothScrollTo(0, 0)
            }

            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupBottomSheet(){

        bottomSheetBehavior = BottomSheetBehavior.from(binding.recipeDetailsBottomSheet)


        bottomSheetCallback = object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.recipeDetailsToolbar.visibility = View.VISIBLE
                        viewModel.bottomSheetBehaviorState =
                            BottomSheetBehaviorStateWrapper.STATE_EXPANDED
                    }
                    else -> {
                        binding.recipeDetailsToolbar.visibility = View.GONE
                        viewModel.bottomSheetBehaviorState =
                            BottomSheetBehaviorStateWrapper.STATE_COLLAPSED
                    }
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

    private fun getDimensionPixelSize():Int{
        return resources.getDimensionPixelSize(R.dimen.grid_2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior = null
        _binding = null
    }
}
