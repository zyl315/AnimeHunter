package com.zyl315.animehunter.util

import android.content.Context
import android.widget.Toast


fun Context.showToast(
    context: Context = applicationContext,
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    MyToast.makeText(context, message, duration)
}

fun Context.showToast(
    context: Context = applicationContext,
    message: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    MyToast.makeText(context, context.getString(message), duration)
}

object MyToast {
    private var lastToastMessage: String = ""
    private var lastToastTime: Long = System.currentTimeMillis()

    fun makeText(context: Context, message: String, duration: Int) {
        if (lastToastMessage == message && System.currentTimeMillis() - lastToastTime < 3000) {
            return
        }
        lastToastTime = System.currentTimeMillis()
        lastToastMessage = message
        Toast.makeText(context, message, duration).show()
    }
}
