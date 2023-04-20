package com.jesse.ohunelo.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

class UiDrawable(@DrawableRes val resId: Int) {
    fun asDrawable(context: Context): Drawable?{
        return ResourcesCompat.getDrawable(context.resources, resId, null)
    }
}