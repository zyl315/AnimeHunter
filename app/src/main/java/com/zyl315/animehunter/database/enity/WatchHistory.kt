package com.zyl315.animehunter.database.enity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zyl315.animehunter.bean.BaseBean
import com.zyl315.animehunter.bean.age.BangumiBean

@Entity
data class WatchHistory(
    @PrimaryKey
    var animeId: String,
    var time: Long,
    var dataSourceIndex: Int,
    var episodeName: String,
    var episodeIndex: Int,
    var duration: Long,
    var watchedPosition: Long,
    @Embedded
    var bangumiBean: BangumiBean
) : BaseBean