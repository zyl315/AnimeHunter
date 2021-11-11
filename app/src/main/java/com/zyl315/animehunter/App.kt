package com.zyl315.animehunter

import android.app.Application
import android.content.Context


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
//
//        VideoViewManager.setConfig(
//            VideoPlayerConfig.newBuilder()
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setLogEnabled(true)
//                .build()
//        )
    }

    companion object {
        var context: Context? = null
    }
}