package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

data class PlaySourceBean(val count: Int = 0, var select: Boolean = false) : BaseBean {
    var episodeList: MutableList<EpisodeBean> = mutableListOf()
}

data class EpisodeBean(val title: String, val href: String, var url: String = "") : BaseBean {
    var purl = ""
    var vurl = ""
    var playEx = ""
    var playId = ""
}