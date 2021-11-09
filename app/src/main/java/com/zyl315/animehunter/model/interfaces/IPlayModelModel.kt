package com.zyl315.animehunter.model.interfaces

import com.zyl315.animehunter.bean.age.PlaySourceBean

interface IPlayModelModel : IBaseModel {
    suspend fun getPlaySource(bangumiId: String): MutableList<PlaySourceBean>

    suspend fun getPlayUrl(url: String, retryCount:Int = 3): String

    companion object {
        const val TAG = "PlayerModel"
    }
}