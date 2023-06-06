package com.jesse.ohunelo.util

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import com.jesse.ohunelo.R
import com.google.android.material.R.attr

open class NoUnderlineClickSpan(private val context: Context) : ClickableSpan() {
    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.isUnderlineText = false
        textPaint.color = MaterialColors.getColor(context, attr.colorSecondaryVariant,
            ContextCompat.getColor(context, R.color.brown_400))
    }

    override fun onClick(widget: View) {}
}