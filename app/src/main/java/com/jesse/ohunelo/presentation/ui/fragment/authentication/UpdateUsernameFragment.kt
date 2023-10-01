package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentUpdateUsernameBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.UpdateUsernameViewModel
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateUsernameFragment : Fragment() {

    private var _binding: FragmentUpdateUsernameBinding? = null
    private val binding: FragmentUpdateUsernameBinding get() =  _binding!!

    private val viewModel by viewModels<UpdateUsernameViewModel>()

    private var loader: LoaderDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_username, container, false)

        binding.apply {
            viewModel = this@UpdateUsernameFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.updateUsernameUiStateFlow.collect{
                        updateUsernameUiState ->
                    if (updateUsernameUiState.navigateToNextScreen){
                        findNavController().navigate(UpdateUsernameFragmentDirections.actionUpdateUsernameFragmentToHomeFragment())
                        viewModel.onNavigationToNextScreen()
                    }
                    if (updateUsernameUiState.showErrorMessage.first){
                        showErrorMessage(updateUsernameUiState.showErrorMessage.second)
                    }
                    // If all other views are not enabled, then the loader should be shown
                    if (!updateUsernameUiState.isEnabled){
                        showLoader()
                    }
                    // If all other views are enabled, then the loader should be hidden
                    else{
                        hideLoader()
                    }
                }
            }
        }
    }

    private fun showErrorMessage(errorMessage: UiText?) {
        Toast.makeText(
            requireContext(),
            errorMessage?.asString(requireContext()),
            Toast.LENGTH_LONG
        ).show()
        viewModel.onErrorMessageShown()
    }

    private fun setOnClickListeners(){
        binding.continueButton.setOnClickListener {
            viewModel.updateUserName()
        }
        binding.updateUsernameToolBar.setNavigationOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun setOnTextChangedListeners(){
        binding.firstNameEditText.addTextChangedListener {
            text: Editable? -> text?.let{
                viewModel.onFirstNameTextChanged(it.toString())
            }
        }

        binding.lastNameEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onLastNameTextChanged(it.toString())
            }
        }
    }

    private fun showLoader(){
        loader?.show(childFragmentManager, LoaderDialogFragment.TAG)
    }

    private fun hideLoader(){
        loader?.let {
                loader ->
            if(loader.isAdded){
                loader.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setOnTextChangedListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loader = null
        _binding = null
    }
}