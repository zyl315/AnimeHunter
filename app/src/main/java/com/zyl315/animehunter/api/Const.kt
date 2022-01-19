package com.zyl315.animehunter.api

object Const {
    object AgeFans {
        var BASE_URL_LIST = listOf("https://www.agefans.vip", "https://www.agefans.live")
        var BASE_URL = BASE_URL_LIST[0]
        var SEARCH_URL = "$BASE_URL/search?query=%s&page=%s"
        var DETAIL_URL = "$BASE_URL/play/%s"
        var PLAY_URL = "/_getplay?aid=\$1&playindex=\$2&epindex=\$3"
        var CATALOG_URL = "$BASE_URL/catalog/"
        var DEFAULT_CATALOG_URL = "$BASE_URL/catalog/all-all-all-all-all-time-1"
    }
}