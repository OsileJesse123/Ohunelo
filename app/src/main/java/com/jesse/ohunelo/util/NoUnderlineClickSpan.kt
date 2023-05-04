package com.jesse.ohunelo.util

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.jesse.ohunelo.R

open class NoUnderlineClickSpan(private val context: Context) : ClickableSpan() {
    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.isUnderlineText = false
        textPaint.color = ContextCompat.getColor(context, R.color.orange_40)
    }

    override fun onClick(widget: View) {}
}