package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentRegisterBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.uistates.RegisterUiState
import com.jesse.ohunelo.presentation.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()

    private var loader: LoaderDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container,
            false)
        binding.apply {
            viewModel = this@RegisterFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = LoaderDialogFragment()

        setOnClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.registerUiStateFlow.collect{
                        registerUiState ->
                    if (registerUiState.navigateToNextScreen){
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToVerifyEmailFragment())
                        viewModel.onNavigationToNextScreen()
                    }
                    if (registerUiState.showErrorMessage.first){
                        showErrorMessage(registerUiState)
                    }
                    // If all other views are not enabled, then the loader should be shown
                    if (!registerUiState.isEnabled){
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

    private fun showErrorMessage(registerUiState: RegisterUiState) {
        Toast.makeText(
            requireContext(),
            registerUiState.showErrorMessage.second?.asString(requireContext()),
            Toast.LENGTH_LONG
        ).show()
        viewModel.onErrorMessageShown()
    }

    private fun setOnClickListeners(){
        binding.continueButton.setOnClickListener {
            viewModel.register()
        }

        binding.loginText.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnTextChangedListeners(){

        binding.firstNameEditText.addTextChangedListener {
                text: Editable? -> text?.let {
                viewModel.onFirstNameTextChanged(it.toString())
            }
        }

        binding.lastNameEditText.addTextChangedListener {
                text: Editable? -> text?.let {
                viewModel.onLastNameTextChanged(it.toString())
            }
        }


        binding.enterEmailAddressEditText.addTextChangedListener {
                text: Editable? -> text?.let {
                viewModel.onEmailTextChanged(it.toString())
            }
        }

        binding.enterPasswordEditText.addTextChangedListener {
                text: Editable? -> text?.let {
                viewModel.onPasswordTextChanged(it.toString())
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