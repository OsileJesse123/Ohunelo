package com.jesse.ohunelo.presentation.ui.fragment.recipe_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.StepsAdapter
import com.jesse.ohunelo.data.network.models.Step
import com.jesse.ohunelo.databinding.FragmentStepsBinding

class StepsFragment() : Fragment() {

    private var _binding: FragmentStepsBinding? = null
    private val binding: FragmentStepsBinding get() = _binding!!

    private var _stepsAdapter: StepsAdapter? = null
    private val stepsAdapter: StepsAdapter get() = _stepsAdapter!!

    private val args by navArgs<StepsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStepsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.stepsToolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        determineWhatToDisplay()

    }

    private fun determineWhatToDisplay(){
        if(args.analyzedInstructions.steps.isEmpty()){
            // If list of steps comes in as empty, display the no steps text
            binding.noStepsText.isVisible = true
        } else {
            // Else setup the recycler and display the items on the screen
            setupRecycler()
        }
    }

    private fun setupRecycler(){
        _stepsAdapter = StepsAdapter()
        binding.stepsRecycler.apply {
            adapter = stepsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,
                false)
        }
        stepsAdapter.submitList(args.analyzedInstructions.steps)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _stepsAdapter = null
        _binding = null
    }
}