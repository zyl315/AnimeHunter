package com.zyl315.animehunter.repository.impls

import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.database.getAppDataBase
import com.zyl315.animehunter.repository.interfaces.IHistoryRepository

class HistoryRepository : IHistoryRepository {
    private val appDatabase by lazy {
        getAppDataBase()
    }

    override suspend fun loadAllWatchHistory(): MutableList<WatchHistory> {
        return appDatabase.watchHistoryDao().loadAllWatchHistory()
    }

    override suspend fun loadWatchHistoryById(bangumiId: String): WatchHistory? {
        return appDatabase.watchHistoryDao().loadWatchHistoryById(bangumiId)
    }

    override suspend fun saveWatchHistory(watchHistory: WatchHistory) {
        return appDatabase.watchHistoryDao().insertWatchHistory(watchHistory)
    }

    override suspend fun deleteWatchHistory(vararg watchHistory: WatchHistory) {
        return appDatabase.watchHistoryDao().deleteWatchHistory(*watchHistory)
    }
}