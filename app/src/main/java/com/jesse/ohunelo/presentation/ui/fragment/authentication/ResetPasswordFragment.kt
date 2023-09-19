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
import com.jesse.ohunelo.databinding.FragmentResetPasswordBinding
import com.jesse.ohunelo.presentation.ui.fragment.dialogs.LoaderDialogFragment
import com.jesse.ohunelo.presentation.viewmodels.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding: FragmentResetPasswordBinding get() = _binding!!

    private val viewModel by viewModels<ResetPasswordViewModel>()

    private var loader: LoaderDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_reset_password,
            container, false)

        binding.apply {
            viewModel = this@ResetPasswordFragment.viewModel
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
                viewModel.resetPasswordUiStateFlow.collect{
                        resetPasswordUiState ->
                    if (resetPasswordUiState.showConfirmationMessage.first){
                        showMessage(resetPasswordUiState.showConfirmationMessage.second?.asString(requireContext()))
                    }
                    if (resetPasswordUiState.showErrorMessage.first){
                        showMessage(resetPasswordUiState.showErrorMessage.second?.asString(requireContext()))
                    }
                    // If all other views are not enabled, then the loader should be shown
                    if (!resetPasswordUiState.isEnabled){
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

    private fun setOnClickListeners() {
        binding.resetPasswordToolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.submitButton.setOnClickListener {
            viewModel.submit()
        }
    }

    private fun showMessage(message: String?){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        viewModel.onMessageShown()
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
        binding.enterEmailAddressEditText.addTextChangedListener {
            text: Editable? -> text?.let {
                viewModel.onEmailTextChanged(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loader = null
        _binding = null
    }

}