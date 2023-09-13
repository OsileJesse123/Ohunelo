package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentVerifyEmailBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.uistates.RegisterUiState
import com.jesse.ohunelo.presentation.uistates.VerifyEmailUiState
import com.jesse.ohunelo.presentation.viewmodels.VerifyEmailViewModel
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyEmailFragment : Fragment() {

    private var _binding: FragmentVerifyEmailBinding? = null
    private val binding: FragmentVerifyEmailBinding get() = _binding!!

    private var loader: LoaderDialogFragment? = null

    private val viewModel by viewModels<VerifyEmailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_email, container, false)
        binding.apply {
            viewModel = this@VerifyEmailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = LoaderDialogFragment()

        setupOnClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.verifyEmailUiState.collect{
                    verifyEmailUiState ->
                    if (verifyEmailUiState.navigateToNextScreen){
                        navigateToNextScreen()
                    }
                    if(!verifyEmailUiState.isEnabled){
                        // If the buttons are not enabled then show the loader
                        showLoader()
                    } else {
                        // Else hide the loader if it is visible
                        hideLoader()
                    }
                    if(verifyEmailUiState.showErrorMessage.first){
                        showErrorMessage(verifyEmailUiState.showErrorMessage.second)
                    }
                }
            }
        }

    }

    private fun setupOnClickListeners(){
        binding.verifyEmailToolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.resendEmailButton.setOnClickListener {
            viewModel.resendEmailLink()
        }
        binding.continueButton.setOnClickListener {
            navigateToNextScreen()
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

    private fun navigateToNextScreen(){
        findNavController()
            .navigate(VerifyEmailFragmentDirections.actionVerifyEmailFragmentToLoginFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loader = null
        _binding = null
    }
}