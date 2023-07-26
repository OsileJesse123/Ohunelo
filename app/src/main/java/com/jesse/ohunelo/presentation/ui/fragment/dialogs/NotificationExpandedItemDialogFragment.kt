package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.databinding.NotificationsExpandedItemBinding

class NotificationExpandedItemDialogFragment(
    private val notification: Notification,
) : DialogFragment() {

    companion object {
        const val TAG = "NotificationExpandedItemDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = NotificationsExpandedItemBinding.inflate(layoutInflater).apply {
                notification = this@NotificationExpandedItemDialogFragment.notification
                executePendingBindings()
            }

            val notificationDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            notificationDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Set dialog size according to screen size
                val mDisplayWidth = resources.displayMetrics.widthPixels
                val mDisplayHeight = resources.displayMetrics.heightPixels
                window?.setLayout((mDisplayWidth * 0.75f).toInt(), (mDisplayHeight * 0.5f).toInt())
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }
}