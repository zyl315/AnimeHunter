package com.zyl315.animehunter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.zyl315.exo.ExoMediaPlayerFactory
import com.zyl315.player.player.VideoViewConfig
import com.zyl315.player.player.VideoViewManager


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this

        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                .setLogEnabled(true)
                .build()
        )
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}