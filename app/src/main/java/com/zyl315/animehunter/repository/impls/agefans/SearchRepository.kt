package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.execption.NoModeDataException
import com.zyl315.animehunter.repository.interfaces.ISearchRepository
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import okhttp3.*
import org.jsoup.Jsoup

class SearchRepository : ISearchRepository {

    override suspend fun getSearchData(
        keyWord: String,
        page: Int
    ): Pair<MutableList<BangumiBean>, String> {

        val request = Request.Builder()
            .url(Const.AgeFans.SEARCH_URL.format(keyWord, page))
            .build()

        val response = MyOkHttpClient.client.newCall(request).execute()

        val document = Jsoup.parse(response.body?.string())

        val totalCount = document.getElementById("result_count")?.text() ?: "0"

        val blockContent =
            document.getElementsByClass("blockcontent1").apply {
                if (this.size == 0) throw NoModeDataException()
            }

        val elements = blockContent[0].getElementsByClass("cell")

        val searchResultList: MutableList<BangumiBean> = mutableListOf()

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

            searchResultList.add(
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

        return Pair(searchResultList, totalCount)
    }
}