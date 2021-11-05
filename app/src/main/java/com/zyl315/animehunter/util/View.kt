package com.zyl315.animehunter.util

import android.view.View
import android.view.animation.AlphaAnimation

fun View.gone(animate: Boolean = false, duration: Long = 500L) {
    visibility = View.GONE
    if (animate) startAnimation(AlphaAnimation(1f, 0f).also { it.duration = duration })
}


fun View.visible(animate: Boolean = false, duration: Long = 500L) {
    visibility = View.VISIBLE
    if (animate) startAnimation(AlphaAnimation(0f, 1f).also { it.duration = duration })
}

fun View.invisible(animate: Boolean = false, duration: Long = 500L) {
    visibility = View.INVISIBLE
    if (animate) startAnimation(AlphaAnimation(1f, 0f).also { it.duration = duration })
}

fun View.clickScale(scale: Float = 0.75f, duration: Long = 100L) {
    animate()
        .scaleX(scale).scaleY(scale).setDuration(duration)
        .withEndAction { animate().scaleX(1f).scaleY(1f).setDuration(duration).start() }
        .start()
}