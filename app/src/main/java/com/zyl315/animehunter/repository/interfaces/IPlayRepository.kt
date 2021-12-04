package com.zyl315.animehunter.repository.interfaces

import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean

interface IPlayRepository : IBaseRepository {
    suspend fun getPlaySource(bangumiId: String): MutableList<PlaySourceBean>

    suspend fun getPlayUrl(url: String, retryCount: Int = 3): String

    suspend fun getPlayUrl(episodeBean: EpisodeBean, retryCount: Int = 3): EpisodeBean

    companion object {
        const val TAG = "PlayerRepository"
    }
}