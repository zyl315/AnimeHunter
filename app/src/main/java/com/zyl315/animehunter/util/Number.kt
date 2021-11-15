package com.zyl315.animehunter.util

import android.util.TypedValue
import com.zyl315.animehunter.App


/**
 * dp转为px
 */
val Number.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        App.context.resources.displayMetrics
    ).toInt()


/**
 * sp转为px
 */
val Number.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        App.context.resources.displayMetrics
    ).toInt()
