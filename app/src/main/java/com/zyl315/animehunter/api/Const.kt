package com.zyl315.animehunter.api

object Const {
    object Common {
        const val GITHUB_URL = "https://github.com/zyl315"
        const val BASE_URL = "http://www.imomoe.live/"
    }

    object AgeFans {
        var BASE_URL_LIST = listOf("https://www.agefans.vip", "https://www.agefans.live")
        var BASE_URL = BASE_URL_LIST[0]
        var SEARCH_URL = "$BASE_URL/search?query=%s&page=%s"
        var DETAIL_URL = "$BASE_URL/play/%s"
        var PLAY_URL = "/_getplay?aid=\$1&playindex=\$2&epindex=\$3"
        var PLAY_REFERER = "$BASE_URL/play/%s?playid=%s"
    }

    object ViewHolderType {
        const val UNKNOWN = -1
        const val EMPTY = 0
        const val BangumiCover = 1
    }
}