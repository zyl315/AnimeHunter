package com.zyl315.animehunter.util

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun Context.showToast(
    context: Context = applicationContext,
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
    mainThread: Boolean = true
) {
    if (mainThread) {
        MyToast.makeText(context, message, duration)
    } else {
        GlobalScope.launch(Dispatchers.Main) {
            MyToast.makeText(context, message, duration)
        }
    }
}

fun Context.showToast(
    context: Context = applicationContext,
    resId: Int,
    duration: Int = Toast.LENGTH_SHORT,
    mainThread: Boolean = true
) {
    showToast(context, context.getString(resId), duration, mainThread)
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
