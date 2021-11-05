package com.zyl315.animehunter.model.interfaces

import com.zyl315.animehunter.bean.age.PlayUrlBean

interface IPlayModelModel :IBaseModel {
    suspend fun getDetailPage(bangumiId: String) :MutableList<PlayUrlBean>

    suspend fun getPlayUrl(bangumiId: String, playIndex: Int, epIndex: Int): String

    companion object {
        const val TAG = "PlayerModel"
    }
}