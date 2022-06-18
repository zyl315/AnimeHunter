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
    var name: String = ""
    var bangumiType: String = ""
    var originalName: String = ""
    var otherName: String = ""
    var premiereTime: String = ""
    var playStatus: String = ""
    var originalWork: String = ""
    var plotType: String = ""
    var productionCompany: String = ""
    var description: String = ""
    var coverUrl: String = ""
    var newName: String = ""
}


data class BangumiWeekListBean(
    val bangumiID: String,
    val dayOfWeek: Int,
    val name: String,
    val isNew: Boolean,
    val updateTime: String,
    val status: String
) : BaseBean
