package com.zyl315.animehunter.database.enity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zyl315.animehunter.bean.BaseBean

@Entity
data class WatchHistory(
    @PrimaryKey
    var bangumiId: String,
    var bangumiTitle: String,
    var coverUrl: String,
    var watchTime: Long,
    var dataSourceIndex: Int,
    var episodeName: String,
    var episodeIndex: Int,
    var episodeUrl: String,
    var duration: Long,
    var watchedPosition: Long
) : BaseBean
