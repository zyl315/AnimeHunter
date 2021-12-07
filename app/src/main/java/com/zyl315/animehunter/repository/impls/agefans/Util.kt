package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.CatalogItemBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.execption.NoModeDataException
import org.jsoup.Jsoup

object Util {
    fun processBangumi(body: String): Pair<MutableList<BangumiBean>, String> {
        val document = Jsoup.parse(body)

        val totalCount = document.getElementById("result_count")?.text() ?: "0"

        val blockContent =
            document.getElementsByClass("blockcontent1").apply {
                if (this.size == 0) throw NoModeDataException()
            }

        val elements = blockContent[0].getElementsByClass("cell")

        val resultList: MutableList<BangumiBean> = mutableListOf()

        elements.forEach { element ->
            var name = ""
            var coverUrl = ""
            var newName = ""
            var bangumiId = ""

            element.select("a").first()?.run {
                name = getElementsByTag("img").attr("alt")
                coverUrl = getElementsByTag("img").attr("src").run {
                    if (startsWith("//")) {
                        return@run replaceFirst("//", "https://")
                    }
                    this
                }
                newName = getElementsByTag("span").text()
                bangumiId = attr("href").replaceFirst("/detail/", "")
            }


            val infoElements = element.getElementsByClass("cell_imform_kv")
            val infoMap = mutableMapOf<String, String>()

            for (info in infoElements) {
                val key: String = info.getElementsByClass("cell_imform_tag").text()
                    .replace("：", "")
                val value: String = if (key == "简介") {
                    info.getElementsByClass("cell_imform_desc").text()
                } else {
                    info.getElementsByClass("cell_imform_value").text()
                }
                infoMap[key] = value
            }

            resultList.add(
                BangumiBean(bangumiId).also {
                    it.name = name
                    it.bangumiType = infoMap["动画种类"] ?: ""
                    it.originalName = infoMap["原版名称"] ?: ""
                    it.otherName = infoMap["其他名称"] ?: ""
                    it.premiereTime = infoMap["首播时间"] ?: ""
                    it.playStatus = infoMap["播放状态"] ?: ""
                    it.originalWork = infoMap["原作"] ?: ""
                    it.plotType = infoMap["剧情类型"] ?: ""
                    it.productionCompany = infoMap["制作公司"] ?: ""
                    it.description = infoMap["简介"] ?: ""
                    it.coverUrl = coverUrl
                    it.newName = newName
                })
        }

        return Pair(resultList, totalCount)
    }

    fun processCatalog(html: String): List<CatalogTagBean> {
        val document = Jsoup.parse(html)

        val searchList = document.getElementById("search-list")?.children()

        val resultList = mutableListOf<CatalogTagBean>()

        searchList?.forEach { li ->
            if (li.tagName() == "li") {
                val tagName = li.child(0).text().replace("：",":")
                val searchTagList = li.child(1).children()

                val catalogTagBean = CatalogTagBean(tagName)

                searchTagList.forEach { item ->
                    val isSelected = item.className() == "on"
                    val a = item.getElementsByTag("a")
                    val href = a.attr("href")
                    val name = a.text()
                    catalogTagBean.catalogItemBeanList.add(
                        CatalogItemBean(name, href, isSelected)
                    )
                }
                resultList.add(catalogTagBean)
            }
        }
        return resultList
    }
}