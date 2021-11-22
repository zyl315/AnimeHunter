package com.zyl315.animehunter.model.interfaces

import com.zyl315.animehunter.database.enity.WatchHistory

interface IHistoryModel : IBaseModel {
    suspend fun loadAllWatchHistory(): MutableList<WatchHistory>

    suspend fun loadWatchHistoryById(bangumiId: String): WatchHistory?

    suspend fun saveWatchHistory(watchHistory: WatchHistory)

    suspend fun deleteWatchHistory(vararg watchHistory: WatchHistory)

    companion object {
        const val TAG = "IHistoryModel"
    }
}