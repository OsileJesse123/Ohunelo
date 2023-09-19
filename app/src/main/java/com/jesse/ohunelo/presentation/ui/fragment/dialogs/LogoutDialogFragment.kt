package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.LogoutDialogFragmentBinding
import java.lang.IllegalStateException

class LogoutDialogFragment(
    private var confirmOnClickListener: (() -> Unit)?,
): DialogFragment() {

    companion object {
        const val TAG = "LogoutDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val binding = LogoutDialogFragmentBinding.inflate(layoutInflater)

            val confirmationDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()


            binding.confirmButton.setOnClickListener {
                    button ->
                button.isEnabled = false
                confirmOnClickListener?.let {
                    it()
                }
                confirmationDialog.dismiss()
            }

            binding.backButton.setOnClickListener {
                    button ->
                button.isEnabled = false
                confirmationDialog.dismiss()
            }

            confirmationDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        confirmOnClickListener = null
    }
}