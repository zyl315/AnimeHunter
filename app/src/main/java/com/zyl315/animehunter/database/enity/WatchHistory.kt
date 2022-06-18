package com.zyl315.animehunter.database.enity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.BaseBean
import com.zyl315.animehunter.bean.age.BangumiDetailBean

@Entity
data class WatchHistory(
    @PrimaryKey
    @ColumnInfo(name = "animeID")
    var bangumiID: String,
    var time: Long,
    var dataSourceIndex: Int,
    var episodeName: String,
    var episodeIndex: Int,
    var duration: Long,
    var watchedPosition: Long,
    @Embedded
    var bangumiCoverBean: BangumiCoverBean
) : BaseBean

