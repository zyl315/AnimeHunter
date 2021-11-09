package com.zyl315.animehunter

import android.app.Application
import android.content.Context
import com.yc.kernel.impl.exo.ExoPlayerFactory
import com.yc.video.config.VideoPlayerConfig
import com.yc.video.player.VideoViewManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this


        VideoViewManager.setConfig(
            VideoPlayerConfig.newBuilder()
                .setPlayerFactory(ExoPlayerFactory.create())
                .setLogEnabled(true)
                .build()
        )
    }

    companion object {
        var context: Context? = null
    }
}