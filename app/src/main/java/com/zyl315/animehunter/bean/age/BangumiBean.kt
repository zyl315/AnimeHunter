package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

data class BangumiBean(
    val bangumiID: String,
    val name: String,
    val newName: String,
    val coverUrl: String? = null
) : BaseBean

data class BangumiDetailBean(
    var bangumiID: String
) : BaseBean {
    lateinit var name: String
    lateinit var bangumiType: String
    lateinit var originalName: String
    lateinit var otherName: String
    lateinit var premiereTime: String
    lateinit var playStatus: String
    lateinit var originalWork: String
    lateinit var plotType: String
    lateinit var productionCompany: String
    lateinit var description: String
    lateinit var coverUrl: String
    lateinit var newName: String
}

