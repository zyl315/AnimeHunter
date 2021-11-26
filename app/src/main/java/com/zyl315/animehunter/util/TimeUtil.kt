package com.zyl315.animehunter.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
val timeInstance = SimpleDateFormat("HH:mm")

@SuppressLint("SimpleDateFormat")
val dateInstance = SimpleDateFormat("MM-dd HH:mm")

@SuppressLint("SimpleDateFormat")
val yearDateInstance = SimpleDateFormat("yy-MM-dd HH:mm")
const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000


fun translateTimeUnit(timeMillis: Long): String {
    val today = Calendar.getInstance()
    val past = Calendar.getInstance().apply {
        timeInMillis = timeMillis
    }
    val timeDifference = today.timeInMillis - past.timeInMillis
    return when {
        timeDifference < ONE_DAY_MILLIS -> {
            "今天 " + timeInstance.format(past.time)
        }
        today.get(Calendar.YEAR) == past.get(Calendar.YEAR) -> {
            dateInstance.format(past.time)
        }
        else -> {
            yearDateInstance.format(past.time)
        }
    }
}

fun translateWatchPosition(lastPosition: Long, totalDuration: Long): String {
    if (totalDuration == 0L) return ""
    val percent = lastPosition / totalDuration.toFloat()
    return when {
        percent < 0.05 -> {
            "刚刚开始"
        }
        percent > 0.95 -> {
            "已看完"
        }
        else -> {
            "观看至${stringForTime(lastPosition)}"
        }
    }
}

/**
 * 格式化时
 */
fun stringForTime(timeMs: Long): String? {
    val totalSeconds = timeMs / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}