package com.jesse.ohunelo.presentation.ui.fragment.recipe_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.window.layout.WindowMetricsCalculator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.ViewPagerAdapter
import com.jesse.ohunelo.databinding.FragmentRecipeDetailsBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.DisplayImageDialogFragment
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
            fragmentViewMargin = getDimensionPixelSize(R.dimen.grid_2)
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

        updateBottomSheetBehaviorState()

        setupOnClickListeners()

        setupLayoutDimension()

        setupViewPager()

    }

    private fun setupLayoutDimension(){
        val screenHeight = getScreenHeight()
        val screenHeightOffset = getDimensionPixelSize(R.dimen.screen_height_medium_offset)
        // If screen height <= 700dp then set bottom sheet peek height to 350dp else set it to 400dp
        bottomSheetBehavior?.peekHeight = if(screenHeight <= screenHeightOffset){
             getDimensionPixelSize(R.dimen.bottom_sheet_peek_height_compact)
        } else{
            getDimensionPixelSize(R.dimen.bottom_sheet_peek_height_medium)
        }
        val bottomSheetPeekHeight = bottomSheetBehavior?.peekHeight ?: 0
        val imageViewNewHeight = screenHeight - bottomSheetPeekHeight
        binding.recipeImageConstraint.layoutParams.height = imageViewNewHeight
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

            binding.recipeImage.setOnClickListener {
                it.isEnabled = false
                DisplayImageDialogFragment(args.recipe.image) {
                    it.isEnabled = true
                }.show(childFragmentManager, DisplayImageDialogFragment.TAG)
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

    private fun getDimensionPixelSize(@DimenRes dimension: Int):Int{
        return resources.getDimensionPixelSize(dimension)
    }

    private fun getScreenHeight(): Int{
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity())
        return metrics.bounds.height()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior?.removeBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior = null
        _binding = null
    }
}
