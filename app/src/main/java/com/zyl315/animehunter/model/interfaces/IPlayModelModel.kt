package com.zyl315.animehunter.model.interfaces

import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean

interface IPlayModelModel : IBaseModel {
    suspend fun getPlaySource(bangumiId: String): MutableList<PlaySourceBean>

    suspend fun getPlayUrl(url: String, retryCount: Int = 3): String

    suspend fun getPlayUrl(episodeBean: EpisodeBean, retryCount: Int = 3): EpisodeBean

    companion object {
        const val TAG = "PlayerModel"
    }
}