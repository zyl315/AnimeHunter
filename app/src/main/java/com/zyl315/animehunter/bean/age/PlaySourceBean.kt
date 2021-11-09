package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

data class PlaySourceBean(val count: Int = 0, var select: Boolean = false) : BaseBean {
    var episodeList: MutableList<EpisodeBean> = mutableListOf()
}

data class EpisodeBean(val title: String, val url: String, var originUrl: String = "") : BaseBean