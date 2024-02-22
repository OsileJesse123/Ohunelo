package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.UpdateProfileDialogFragmentBinding
import com.jesse.ohunelo.presentation.viewmodels.UpdateProfileViewModel
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.updateProfileUiState.collect{
                    updateProfileUiState ->
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}