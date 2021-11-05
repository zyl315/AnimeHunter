package com.zyl315.animehunter

import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        println("1")
    }

    companion object {
        var context: Context? = null
    }
}