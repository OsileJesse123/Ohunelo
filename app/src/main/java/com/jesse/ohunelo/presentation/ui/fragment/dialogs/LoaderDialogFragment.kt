package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.LoaderDialogFragmentBinding

class LoaderDialogFragment: DialogFragment() {

    companion object {
        const val TAG = "LoaderDialogFragment"
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = LoaderDialogFragmentBinding.inflate(layoutInflater)

            val loaderDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            loaderDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCanceledOnTouchOutside(false)

                // Set dialog size according to screen size
                /*val mDisplayWidth = resources.displayMetrics.widthPixels
                val mDisplayHeight = resources.displayMetrics.heightPixels
                window?.setLayout((mDisplayWidth * 0.75f).toInt(), (mDisplayHeight * 0.5f).toInt())*/
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }
}