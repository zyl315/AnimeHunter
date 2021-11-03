package com.zyl315.animehunter.bean

import java.io.Serializable

data class AgeBangumiBean(
    var bangumiID: String
) : Serializable {
    var name: String = "";
    var bangumiType: String = "";
    var originalName: String = "";
    var otherName: String = "";
    var premiereTime: String = "";
    var playStatus: String = "";
    var originalWork: String = "";
    var plotType: String = "";
    var productionCompany: String = "";
    var description: String = "";
    var coverUrl: String = "";
    var newName: String = "";
}
