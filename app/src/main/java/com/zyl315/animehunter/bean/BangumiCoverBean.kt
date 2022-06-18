package com.zyl315.animehunter.bean

import com.zyl315.animehunter.repository.datasource.DataSourceManager

data class BangumiCoverBean(
    val dataSource: DataSourceManager.DataSource,
    val bangumiID: String,
    val title: String,
    val coverUrl: String,
    val type: String,
    val status: String,
    val lastUpdateTime: String
) : BaseBean
