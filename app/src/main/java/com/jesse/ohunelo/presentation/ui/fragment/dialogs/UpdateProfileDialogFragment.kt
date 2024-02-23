package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.UpdateProfileDialogFragmentBinding
import com.jesse.ohunelo.presentation.viewmodels.UpdateProfileViewModel
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateProfileDialogFragment: DialogFragment() {

    companion object {
        const val TAG = "UpdateProfileDialogFragment"
    }

    private var _binding: UpdateProfileDialogFragmentBinding? = null
    private val binding: UpdateProfileDialogFragmentBinding get() = _binding!!

    private val viewModel by viewModels<UpdateProfileViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         return activity?.let {

            _binding = UpdateProfileDialogFragmentBinding.inflate(layoutInflater)

             binding.apply {
                 viewModel = this@UpdateProfileDialogFragment.viewModel
                 lifecycleOwner = this@UpdateProfileDialogFragment
                 executePendingBindings()
             }

             binding.updateButton.setOnClickListener {
                 viewModel.updateProfile()
             }

             this.lifecycleScope.launch {
                 repeatOnLifecycle(Lifecycle.State.STARTED){
                     viewModel.updateProfileUiState.collect{
                             updateProfileUiState ->
                         if (updateProfileUiState.exitUpdateProfile){
                             dismiss()
                         }
                         if (updateProfileUiState.showErrorMessage.first){
                             showMessage(updateProfileUiState.showErrorMessage.second){viewModel.onErrorMessageShown()}
                         }
                         if (updateProfileUiState.showSuccessMessage.first){
                             showMessage(updateProfileUiState.showSuccessMessage.second){viewModel.onSuccessMessageShown()}
                         }
                     }
                 }
             }

            val profileDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            profileDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun showMessage(message: UiText?, onMessageShown: () -> Unit) {
        Toast.makeText(
            requireContext(),
            message?.asString(requireContext()),
            Toast.LENGTH_LONG
        ).show()
        onMessageShown()
    }

    private fun setOnTextChangedListener(){

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

    }

    override fun onResume() {
        super.onResume()
        setOnTextChangedListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}