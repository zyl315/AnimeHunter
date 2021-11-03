package com.zyl315.animehunter.bean

interface BangumiBean : BaseBean {
    var title: String
    var alias: String
    var coverUrl: String
    var detailUrl: String
    var episode: String
    var describe: String
    var bangumiID: String
}

data class BangumiCoverBean(
    override var type: String,
    override var actionUrl: String,
    override var title: String,
    override var alias: String,
    override var coverUrl: String,
    override var detailUrl: String,
    override var episode: String,
    override var describe: String,
    override var bangumiID: String,
) : BangumiBean


data class BangumiDetailBean(
    override var type: String,
    override var actionUrl: String,
    override var title: String,
    override var alias: String,
    override var coverUrl: String,
    override var detailUrl: String,
    override var episode: String,
    override var describe: String,
    override var bangumiID: String,
    var rating: String,
    var pullTime: String,
    var tag: String,
    var area: String,
    var episodeCount: Int
) : BangumiBean