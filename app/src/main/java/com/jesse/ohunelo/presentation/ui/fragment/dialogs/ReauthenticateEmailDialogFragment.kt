package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.ReauthenticateEmailDialogFragmentBinding
import com.jesse.ohunelo.presentation.viewmodels.ReauthenticateEmailViewModel
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReauthenticateEmailDialogFragment(
    private val onDismiss: () -> Unit,
): DialogFragment() {

    companion object {
        const val TAG = "ReauthenticateEmailDialogFragment"
    }

    private var _binding: ReauthenticateEmailDialogFragmentBinding? = null
    private val binding: ReauthenticateEmailDialogFragmentBinding get() = _binding!!

    private val viewModel by viewModels<ReauthenticateEmailViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = ReauthenticateEmailDialogFragmentBinding.inflate(layoutInflater)

            binding.apply {
                viewModel = this@ReauthenticateEmailDialogFragment.viewModel
                lifecycleOwner = this@ReauthenticateEmailDialogFragment
                executePendingBindings()
            }

            binding.reauthenticateButton.setOnClickListener {
                viewModel.reauthenticate()
            }

            this.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.reauthenticateEmailUiState.collectLatest {
                        reauthenticateEmailUiState ->
                        if (reauthenticateEmailUiState.dismiss){
                            dismiss()
                        }
                        reauthenticateEmailUiState.message?.let {
                            message ->
                            showMessage(message){viewModel.onMessageShown()}
                        }
                    }
                }
            }

            val reauthenticateDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            reauthenticateDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity can't be null")
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

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss()
        super.onDismiss(dialog)
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