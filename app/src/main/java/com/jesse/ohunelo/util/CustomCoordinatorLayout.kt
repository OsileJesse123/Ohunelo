package com.jesse.ohunelo.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout

class CustomCoordinatorLayout(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Propagate the touch event to the parent CoordinatorLayout
        parent?.requestDisallowInterceptTouchEvent(true)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Propagate the touch event to the parent CoordinatorLayout
        parent?.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(ev)
    }
}