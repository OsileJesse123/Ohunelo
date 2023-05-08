package com.jesse.ohunelo.presentation.ui.fragment.recipe_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesse.ohunelo.R
import com.jesse.ohunelo.adapters.InstructionsAdapter
import com.jesse.ohunelo.data.network.models.AnalyzedInstructions
import com.jesse.ohunelo.databinding.FragmentInstructionsBinding


class InstructionsFragment(private val analyzedInstructions:List<AnalyzedInstructions>,
                            private val onAnalyzedInstructionClicked:
                            ((analyzedInstruction: AnalyzedInstructions) -> Unit)
                           ) : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding: FragmentInstructionsBinding get() = _binding!!

    private var _instructionsAdapter: InstructionsAdapter? = null
    private val instructionsAdapter: InstructionsAdapter get() = _instructionsAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstructionsBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemCount.text = resources.getQuantityString(R.plurals.number_of_items_available,
            analyzedInstructions.size, analyzedInstructions.size)

        determineWhatToDisplay()

    }

    private fun determineWhatToDisplay(){
        if (analyzedInstructions.isEmpty()){
            // If analyzed instructions comes in as empty, display the no instructions text
            binding.noInstructionsText.isVisible = true
        } else{
            // Else setup the recycler and display the items on the screen
            setupRecycler()
        }
    }

    private fun setupRecycler(){
        _instructionsAdapter = InstructionsAdapter{
            analyzedInstructions ->
            onAnalyzedInstructionClicked(analyzedInstructions)
        }

        binding.instructionsRecycler.apply {
            adapter = instructionsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,
                false)
        }

        instructionsAdapter.submitList(analyzedInstructions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _instructionsAdapter = null
        _binding = null
    }

}