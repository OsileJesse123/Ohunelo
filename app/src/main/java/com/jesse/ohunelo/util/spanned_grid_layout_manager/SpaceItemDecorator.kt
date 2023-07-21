package com.jesse.ohunelo.util.spanned_grid_layout_manager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/** This class was created specifically for the SpannedGridLayoutManager to properly space out the
items in the recycler view.**/
class SpaceItemDecorator(private val spacing: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val rightPosition = (position + 3)
        val otherRightPosition = (position + 2)

        outRect.top = spacing / 2

        if(position % 6 == 4 || rightPosition % 6 == 4 || otherRightPosition % 6 == 4){
            outRect.right = spacing
            outRect.left = spacing / 2
        } else{
            outRect.left = spacing
        }

    }

}