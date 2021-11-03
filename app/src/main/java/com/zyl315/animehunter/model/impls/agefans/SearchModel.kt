package com.zyl315.animehunter.model.impls.agefans

import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.bean.AgeBangumiBean
import com.zyl315.animehunter.model.interfaces.ISearchModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class SearchModel : ISearchModel {
    override suspend fun getSearchData(keyWord: String, page: Int): List<AgeBangumiBean> {
        var document: Document? = null
        try {
            document = Jsoup.connect(Const.Common.AGE_SEARCH_URL)
                .data(mapOf("query" to keyWord, "page" to page.toString()))
                .get()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val elements = document?.getElementsByClass("blockcontent1")?.get(0)
            ?.getElementsByClass("cell")
        val searchResultList: MutableList<AgeBangumiBean> = mutableListOf()

        if (elements != null) {
            for (element in elements) {

                val first = element.select("a").first()
                val name = first?.getElementsByTag("img")?.attr("alt")
                val coverUrl = first?.getElementsByTag("img")?.attr("src")
                val newName = first?.getElementsByTag("span")?.text()
                val bangumiId = first?.attr("href") ?: ""

                val infoElements = element.getElementsByClass("cell_imform_kv")
                val infoMap = mutableMapOf<String, String>()

                for (info in infoElements) {
                    val key: String = info.getElementsByClass("cell_imform_tag").text()
                        .replace("：", "")
                    val value: String = if (key == "简介") {
                        info.getElementsByClass("cell_imform_desc").text()
                    } else {
                        info.getElementsByClass("cell_imform_tag").text()
                    }
                    infoMap[key] = value
                }

                searchResultList.add(
                    AgeBangumiBean(bangumiId).also {
                        it.name = name ?: ""
                        it.bangumiType = infoMap["动画种类"] ?: ""
                        it.originalName = infoMap["原版名称"] ?: ""
                        it.otherName = infoMap["其他名称"] ?: ""
                        it.premiereTime = infoMap["首播时间"] ?: ""
                        it.playStatus = infoMap["播放状态"] ?: ""
                        it.originalWork = infoMap["原作"] ?: ""
                        it.plotType = infoMap["剧情类型"] ?: ""
                        it.productionCompany = infoMap["制作公司"] ?: ""
                        it.description = infoMap["简介"] ?: ""
                        it.coverUrl = coverUrl ?: ""
                        it.newName = newName ?: ""
                    })
            }
        }

        return searchResultList
    }
}