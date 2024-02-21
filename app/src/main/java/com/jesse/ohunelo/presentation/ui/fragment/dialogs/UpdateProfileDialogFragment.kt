package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.UpdateProfileDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateProfileDialogFragment(): DialogFragment() {
    companion object {
        const val TAG = "UpdateProfileDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         return activity?.let {
            val binding = UpdateProfileDialogFragmentBinding.inflate(layoutInflater)

            val profileDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            profileDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }
}