package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.SearchResultBean
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.interfaces.ISourceRepository
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class AgeFansRepository : ISourceRepository {

    private val mBangumiList: MutableList<BangumiBean> = mutableListOf()
    private var totalCount = 0

    override suspend fun getSearchData(
        keyword: String,
        page: Int
    ): RequestState<SearchResultBean> {
        return withContext(Dispatchers.IO) {
            val url = "search?query=$keyword&page=$page"
            val doc = runCatching {
                Jsoup.parse(MyOkHttpClient.getDoc(packUrl(url)))
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }

            runCatching {
                if (page == 1) {
                    mBangumiList.clear()
                    val resultCount = doc.getElementById("result_count")?.text() ?: "0"
                    totalCount = Regex("\\d+").find(resultCount)!!.value.toInt()
                }
                doc.select("div#container div.baseblock div.cell").forEach {
                    val bangumiId = it.child(0).attr("href").replaceFirst("/detail/", "")
                    val coverUrl = packUrl(it.child(0).child(0).attr("src"))
                    val title = it.child(0).child(0).attr("alt")
                    val newName = it.child(0).child(1).text()

                    val infoList = mutableListOf<String>()
                    it.select("div.cell_imform_kv").forEach { cell ->
                        infoList.add(cell.child(1).text())
                    }

                    mBangumiList.add(BangumiBean(bangumiId).apply {
                        this.name = title
                        this.coverUrl = coverUrl
                        this.newName = newName
                        this.bangumiType = infoList.getOrElse(0) { "" }
                        this.originalName = infoList.getOrElse(1) { "" }
                        this.otherName = infoList.getOrElse(2) { "" }
                        this.premiereTime = infoList.getOrElse(3) { "" }
                        this.playStatus = infoList.getOrElse(4) { "" }
                        this.originalWork = infoList.getOrElse(5) { "" }
                        this.plotType = infoList.getOrElse(6) { "" }
                        this.productionCompany = infoList.getOrElse(7) { "" }
                        this.description = infoList.getOrElse(8) { "" }
                    })
                }
                return@withContext RequestState.Success(
                    SearchResultBean(
                        totalCount,
                        page,
                        totalCount == mBangumiList.size,
                        mBangumiList
                    )
                )
            }.onFailure {
                return@withContext RequestState.Error(it)
            }
            return@withContext RequestState.Error(Exception("Unknown Error"))
        }
    }

    companion object {
        var BASE_URL = "https://www.agefans.vip"

        fun packUrl(url: String): String {
            return when {
                url.startsWith("http") -> url
                url.startsWith("//") -> "https:$url"
                url.startsWith("/") -> "$BASE_URL$url"
                else -> "$BASE_URL/$url"
            }
        }
    }
}