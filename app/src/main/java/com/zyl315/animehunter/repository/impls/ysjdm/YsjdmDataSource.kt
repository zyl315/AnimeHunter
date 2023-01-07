package com.zyl315.animehunter.repository.impls.ysjdm

import android.webkit.WebView
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.*
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSource
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.ui.widget.MyWebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.ref.WeakReference

class YsjdmDataSource : AbstractDataSource() {
    private val mBangumiCoverBeanList: MutableList<BangumiCoverBean> = mutableListOf()

    override fun getHost(): String {
        return BASE_URL
    }

    override fun getDefaultCatalogUrl(): String {
        return DEFAULT_CATALOG_URL
    }

    override suspend fun checkHost() {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchData(keyword: String, page: Int): RequestState<SearchResultBean> {
        if (page == 1) mBangumiCoverBeanList.clear()
        val url = packUrl("/index.php/vod/search.html?wd=${keyword}&page=${page}")
        return withContext(Dispatchers.IO) {
            runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(url))
                val totalCount = matchDigital(doc.select(".foot.foot_nav").prev().html())

                doc.select("div.search_box li.searchlist_item").forEach {
                    val bangumiId = matchDigital(it.child(0).child(0).attr("href")).toString()
                    val coverUrl = packUrl(it.child(0).child(0).attr("data-original"))
                    val title = it.child(0).child(0).attr("title")
                    val status = it.select("span.pic_text.text_right").text()
                    val type = it.select("p.vodlist_sub")[0].text()
                    val lastUpdateTime = it.select("p.vodlist_sub")[1].text()
                    mBangumiCoverBeanList.add(
                        BangumiCoverBean(
                            DataSourceManager.DataSource.YSJDM, bangumiId, title, coverUrl, type, status, lastUpdateTime
                        )
                    )
                }
                return@withContext RequestState.Success(
                    SearchResultBean(
                        totalCount, page, totalCount == mBangumiCoverBeanList.size, mBangumiCoverBeanList
                    )
                )
            }.getOrElse {
                return@withContext RequestState.Error(it)
            }
        }
    }

    override suspend fun getBangumi(url: String, page: Int): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getMoreBangumi(url: String, page: Int): RequestState<SearchResultBean> {
        return getCatalog(url + "?page=${page}", page)
    }

    override suspend fun getCatalog(html: String, page: Int): RequestState<SearchResultBean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val catalogUrl = packUrl(html)
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(catalogUrl))
                val catalogList = mutableListOf<CatalogTagBean>()
                val elementList = doc.select("ul.screen_list")
                elementList.forEach { li ->
                    val tagName = if (li == elementList.last()) {
                        "排序"
                    } else {
                        li.select("li > span.text_muted")[0].text()
                    }
                    val catalogTagBean = CatalogTagBean(tagName)
                    li.select("li a").forEach {
                        val isSelected = if (li == elementList.last()) {
                            it.parent()!!.hasClass("hl_fl")
                        } else {
                            it.parent()!!.hasClass("hl")
                        }

                        val href = it.attr("href")
                        val name = it.text()
                        catalogTagBean.catalogItemBeanList.add(
                            CatalogItemBean(name, href, isSelected)
                        )
                    }
                    catalogList.add(catalogTagBean)
                }

                val searchResultBean = processBangumiHtml(doc, page)
                searchResultBean.catalogTagList = catalogList
                return@withContext RequestState.Success(searchResultBean)
            }.getOrElse {
                return@withContext RequestState.Error(it)
            }
        }
    }

    override suspend fun getCatalog(
        url: String, webView: WeakReference<WebView>, webClient: MyWebViewClient
    ): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaySource(bangumiId: String): RequestState<PlayDetailResultBean> {
        val url = packUrl("/index.php/vod/detail?id=$bangumiId")
        return withContext(Dispatchers.IO) {
            runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(url))
                val playSourceBean = PlaySourceBean(0, true)
                doc.select("div.playlist_full ul.content_playlist li").forEach {
                    val title = it.select("a").text()
                    val href = it.select("a").attr("href")
                    playSourceBean.episodeList.add(EpisodeBean(title, href))
                }

                val desc = doc.select("div.content_desc.context span").text()

                val playSourceList: MutableList<PlaySourceBean> = mutableListOf()
                playSourceList.add(playSourceBean)

                val bangumiDetailBean = BangumiDetailBean(bangumiId).apply {
                    description = desc
                }
                return@withContext RequestState.Success(
                    PlayDetailResultBean(
                        playSourceList, bangumiDetailBean
                    )
                )
            }.getOrElse {
                return@withContext RequestState.Error(it)
            }
        }
    }

    override suspend fun getPlayUrl(url: String, retryCount: Int): RequestState<String> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(packUrl(url)))
                val jsonStr = Regex("\\{.*\\}").find(doc.select("div.play_box script")[0].html())!!.value
                val playJson = JSONObject(jsonStr)
                val playUrl = playJson.getString("url")
                if (playUrl.contains("onopen")) {
                    return@withContext RequestState.Success(getEncryptedPlayUrl(playUrl))
                }
                return@withContext RequestState.Success(playUrl)
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }
        }
    }

    fun getEncryptedPlayUrl(url: String): String {
        val doc = Jsoup.parse(MyOkHttpClient.getDoc(ENCRYPTED_URL + url, mapOf("Referer" to BASE_URL)))
        val jsonStr = Regex("\"id\":.*\"").find(doc.select("body script")[0].html())!!.value
        val jsonObj = JSONObject("{$jsonStr}")
        val id = jsonObj.getString("id")
        return "https://bf3.sbdm.cc/runtime/Aliyun/${id}.m3u8"
    }

    override fun getCatalogUrl(url: String): String {
        return url
    }

    override suspend fun getHomeContent(): RequestState<HomeResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeeklyPlayList(): RequestState<WeeklyPlayListBean> {
        TODO("Not yet implemented")
    }

    private fun processBangumiHtml(doc: Element, page: Int = 1): SearchResultBean {
        if (page == 1) {
            mBangumiCoverBeanList.clear()
        }
        val totalCount = matchDigital(doc.select(".foot.foot_nav").prev().html())
        doc.select("div.row li.vodlist_item a.vodlist_thumb").forEach {
            val bangumiId = matchDigital(it.attr("href")).toString()
            val coverUrl = packUrl(it.attr("data-original"))
            val title = it.attr("title")
            val status = it.select("span.pic_text.text_right").text()

            mBangumiCoverBeanList.add(
                BangumiCoverBean(
                    DataSourceManager.DataSource.YSJDM, bangumiId, title, coverUrl, "", status, ""
                )
            )
        }
        return SearchResultBean(
            totalCount, page, totalCount == mBangumiCoverBeanList.size, mBangumiCoverBeanList
        )
    }

    companion object {
        var BASE_URL = "https://www.gqdm.net"
        var DEFAULT_CATALOG_URL = "${BASE_URL}/index.php/vod/show/id/20.html"
        var BASE_SOURCE_URL = "https://bf.sbdm.cc/"
        var ENCRYPTED_URL = "https://bf.sbdm.cc/m3u8.php?url="

        fun packUrl(url: String): String {
            return when {
                url.startsWith("http") -> url
                url.startsWith("//") -> "https:$url"
                url.startsWith("/") -> "$BASE_URL$url"
                else -> "$BASE_URL/$url"
            }
        }

        fun matchDigital(text: String): Int {
            return Regex("\\d+").find(text)!!.value.toInt()
        }

    }
}

fun main() {
    runBlocking {
        launch {
            val res = YsjdmDataSource().getCatalog(YsjdmDataSource.DEFAULT_CATALOG_URL, 1)
            print(res)
        }
    }
}