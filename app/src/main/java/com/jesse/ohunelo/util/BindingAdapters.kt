package com.jesse.ohunelo.util

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView

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
