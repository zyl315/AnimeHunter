package com.zyl315.animehunter.database.dao

import androidx.room.*
import com.zyl315.animehunter.database.enity.WatchHistory

interface WatchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWatchHistory(vararg watchHistory: WatchHistory)

    @Update
    fun updateWatchHistory(vararg watchHistory: WatchHistory)

    @Delete
    fun deleteWatchHistory(vararg watchHistory: WatchHistory)

    @Query("DELETE FROM WatchHistory")
    fun deleteAllWatchHistory()

    @Query("SELECT * FROM WatchHistory ORDER BY time DESC")
    fun loadAllWatchHistory(): MutableList<WatchHistory>

    @Query("SELECT * FROM WatchHistory WHERE bangumiId = :bangumiId")
    fun loadWatchHistoryById(bangumiId: String): WatchHistory?

}