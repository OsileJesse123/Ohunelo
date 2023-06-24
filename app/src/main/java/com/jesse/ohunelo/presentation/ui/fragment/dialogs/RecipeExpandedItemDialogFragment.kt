package com.jesse.ohunelo.presentation.ui.fragment.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jesse.ohunelo.data.model.Recipe
import com.jesse.ohunelo.databinding.SeeAllRecipesExpandedItemBinding
import java.lang.IllegalStateException

class RecipeExpandedItemDialogFragment(
    private val selectedRecipe: Recipe,
    private val navigateToRecipeDetails: ((recipe: Recipe) -> Unit)
): DialogFragment() {

    companion object {
        const val TAG = "RecipeExpandedItemDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = SeeAllRecipesExpandedItemBinding.inflate(layoutInflater).apply {
                recipe = selectedRecipe
                moreDetailsTextView.setOnClickListener {
                    navigateToRecipeDetails(selectedRecipe)
                    dismiss()
                }
                executePendingBindings()
            }

            val recipeDialog = MaterialAlertDialogBuilder(it)
                .setView(binding.root)
                .show()

            recipeDialog.apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Set dialog size according to screen size
                val mDisplayWidth = resources.displayMetrics.widthPixels
                val mDisplayHeight = resources.displayMetrics.heightPixels
                window?.setLayout((mDisplayWidth * 0.75f).toInt(), (mDisplayHeight * 0.65f).toInt())
            }
        } ?: throw IllegalStateException("Activity can't be null")
    }
}