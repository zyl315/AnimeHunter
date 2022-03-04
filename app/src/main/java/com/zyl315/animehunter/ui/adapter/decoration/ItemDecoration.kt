package com.zyl315.animehunter.ui.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.util.dp

class BangumiItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val spanSize = layoutParams.spanSize
        val spanIndex = layoutParams.spanIndex

        if (spanSize == 1) {
            when (spanIndex) {
                0 -> {
                    outRect.left = 8.dp
                }

                1 -> {
                    outRect.left = 4.dp
                    outRect.right = 4.dp
                }

                2-> {
                    outRect.left = 0.dp
                }
            }
        }
    }
}