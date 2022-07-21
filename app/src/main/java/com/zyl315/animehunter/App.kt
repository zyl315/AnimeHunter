package com.zyl315.animehunter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSourceFactory
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

        DataSourceManager.dataSource= AgeFansDataSourceFactory().createDataSource()

//        DarkModeUtils.init(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}