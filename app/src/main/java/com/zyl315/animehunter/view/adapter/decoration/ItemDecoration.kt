package com.zyl315.animehunter.view.adapter.decoration

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
                // 16+x=y+5/2
                // x+y=5
                // x=-4.25 y=9.25
                0 -> {
                    outRect.left = 16.dp
                    outRect.right = -(4.25f).dp // -4.25
                }
                1 -> {
                    outRect.left = 9.25f.dp   // 9.25
                    outRect.right = 2.5f.dp   // 测试机5dp==15px
                }
                2 -> {
                    outRect.left = 2.5f.dp
                    outRect.right = 9.25f.dp  // 9.25
                }
                3 -> {
                    outRect.left = -(4.25f).dp   // -4.25
                    outRect.right = 16.dp
                }
            }
        }
    }
}