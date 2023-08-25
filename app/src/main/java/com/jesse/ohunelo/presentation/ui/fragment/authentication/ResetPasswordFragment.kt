package com.jesse.ohunelo.presentation.ui.fragment.authentication

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jesse.ohunelo.R
import com.jesse.ohunelo.databinding.FragmentResetPasswordBinding
import com.jesse.ohunelo.presentation.viewmodels.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding: FragmentResetPasswordBinding get() = _binding!!

    private val viewModel by viewModels<ResetPasswordViewModel>()

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

        setOnClickListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.resetPasswordUiStateFlow.collect{
                        resetPasswordUiState ->
                    if (resetPasswordUiState.showConfirmationMessage){
                        // This will be changed when proper implementation has been added
                        Timber.e("Confirmation Message: Email has been sent")
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
        _binding = null
    }

}