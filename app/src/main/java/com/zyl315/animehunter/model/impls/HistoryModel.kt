package com.zyl315.animehunter.model.impls

import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.database.getAppDataBase
import com.zyl315.animehunter.model.interfaces.IHistoryModel

class HistoryModel : IHistoryModel {
    private val appDatabase by lazy {
        getAppDataBase()
    }

    override suspend fun loadAllWatchHistory(): MutableList<WatchHistory> {
        return appDatabase.watchHistoryDao().loadAllWatchHistory()
    }

    override suspend fun saveWatchHistory(watchHistory: WatchHistory) {
        return appDatabase.watchHistoryDao().insertWatchHistory(watchHistory)
    }
}