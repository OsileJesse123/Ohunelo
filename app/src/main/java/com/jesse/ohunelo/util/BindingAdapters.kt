package com.jesse.ohunelo.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter

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