package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.databinding.DisplayImageDialogFragmentBinding

class DisplayImageDialogFragment(
    private val imageUrl: String,
    private val reEnableImageListener: (() -> Unit)? = null
): DialogFragment() {

    companion object{
        const val TAG = "DisplayImageDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = DisplayImageDialogFragmentBinding.inflate(layoutInflater)

            binding.apply {
                imageUrl = this@DisplayImageDialogFragment.imageUrl
                executePendingBindings()
            }

            val loaderDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            loaderDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Set dialog size according to screen size
                /*val mDisplayWidth = resources.displayMetrics.widthPixels
                val mDisplayHeight = resources.displayMetrics.heightPixels
                window?.setLayout((mDisplayWidth * 0.75f).toInt(), (mDisplayHeight * 1f).toInt())*/
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        reEnableImageListener?.let {
            reEnableImageListener ->
            reEnableImageListener()
        }
    }
}