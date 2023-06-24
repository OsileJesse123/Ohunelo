package com.jesse.ohunelo.util

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.paging.LoadState
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.jesse.ohunelo.presentation.ui.fragment.OnRecipeCategorySelectedListener

@BindingAdapter("app:uiText")
fun setUiText(view: TextView, uiText: UiText?){
    uiText?.let {
        uiText ->
        view.apply { text = uiText.asString(context) }
    }
}

@BindingAdapter("app:uiDrawable")
fun setUiDrawable(view: ImageView, uiDrawable: UiDrawable?){
    uiDrawable?.let {
        uiDrawable -> view.apply { setImageDrawable(ResourcesCompat
        .getDrawable(resources, uiDrawable.resId, null)) }
    }
}

@BindingAdapter("app:resizableText")
fun setResizableText(view: TextView, recipeDescription: String?){
    recipeDescription?.let {
        recipeDescription ->
        view.setResizableText(recipeDescription, 2, true)
    }
}

@BindingAdapter("app:viewBackgroundColor")
fun setViewBackgroundColor(view: View, color: Int?){
    color?.let{
        color ->
        when(view){
            is MaterialCardView -> view.setCardBackgroundColor(color)
            is TextView -> view.setTextColor(color)
            else -> view.backgroundTintList = ColorStateList.valueOf(color)
        }
    }
}

@BindingAdapter("app:marginStartInt", "app:marginEndInt", requireAll = false)
fun setMarginHorizontalInt(view: View, marginStartInteger: Int? = null, marginEndInteger: Int? = null){

    marginStartInteger?.let {
        marginStartInteger ->
        // Get the existing layout params of the view
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams

        // Set the start margin
        layoutParams.marginStart = marginStartInteger

        // Apply the updated layout params to the view
        view.layoutParams = layoutParams
    }
    marginEndInteger?.let {
        marginEndInteger ->
        // Get the existing layout params of the view
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams

        // Set the end margin
        layoutParams.marginEnd = marginEndInteger

        // Apply the updated layout params to the view
        view.layoutParams = layoutParams
    }

}

@BindingAdapter("app:onRecipeCategorySelected")
fun onRecipeCategorySelected(view: Chip, onRecipeCategorySelectedListener: OnRecipeCategorySelectedListener?){
    view.setOnClickListener {
        onRecipeCategorySelectedListener?.let {
                onRecipeCategorySelectedListener ->
            onRecipeCategorySelectedListener.onRecipeCategorySelected(view.text.toString())
        }
    }
}

@BindingAdapter("app:viewVisibility")
fun setViewVisibility(view: View, loadState: LoadState?){
    loadState?.let {
        loadState ->
        if (view is ProgressBar){
            view.isVisible = loadState is LoadState.Loading
        } else {
            view.isVisible = loadState is LoadState.Error
        }
    }
}


