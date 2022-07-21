package com.zyl315.animehunter.repository.impls.kudm

import android.text.Html
import android.webkit.WebView
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.*
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager.DataSource
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.ui.widget.MyWebViewClient
import com.zyl315.animehunter.util.URLCodeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.net.URLEncoder

class KudmDataSource : AbstractDataSource() {
    private val mBangumiCoverBeanList: MutableList<BangumiCoverBean> = mutableListOf()

    override fun getHost(): String = BASE_URL

    override fun getDefaultCatalogUrl(): String {
        TODO("Not yet implemented")
    }

    override suspend fun checkHost() {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchData(keyword: String, page: Int): RequestState<SearchResultBean> {
        if (page == 1) mBangumiCoverBeanList.clear()
        val searWord = URLEncoder.encode(keyword, "GBK")
        val searchUrl = packUrl("/search.asp?page=$page&searchword=$searWord&searchtype=-1")
        return withContext(Dispatchers.IO) {
            runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(packUrl(searchUrl), isGBK = true))

                val totalInfo = Regex("\\u5171 \\d+ \\u6761").find(doc.select("div.box700.fl h3.titbar")[0].text())

                val totalCount = Regex("\\d+").find(totalInfo!!.value)!!.value.toInt()

                doc.select("div.movie-chrList ul li").forEach {
                    val bangumiId = it.child(0).child(0).attr("href")
                    val coverUrl = packUrl(it.select("img")[0].attr("src"))
                    val title = it.select("img")[0].attr("alt")
                    val status = it.select("em")[0].text()
                    val type = it.select("em")[1].text().replace("主演", "类型")
                    val lastUpdateTime = it.select("em")[2].text()

                    mBangumiCoverBeanList.add(
                        BangumiCoverBean(
                            DataSource.KUDM, bangumiId, title, coverUrl, type, status, lastUpdateTime
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
        Html.fromHtml("")
    }

    override suspend fun getMoreBangumi(url: String, page: Int): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getCatalog(html: String, page: Int): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getCatalog(
        url: String, webView: WeakReference<WebView>, webClient: MyWebViewClient
    ): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaySource(bangumiId: String): RequestState<PlayDetailResultBean> {
        val url = packUrl("${bangumiId}/v.html")
        return withContext(Dispatchers.IO) {
            runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(url))
                val sourceUrl = packUrl(doc.select("div#ccplay")[0].child(0).attr("src"))
                val videListJson = MyOkHttpClient.getDoc(sourceUrl)
                val res: Sequence<MatchResult> = Regex("'[^,]*'").findAll(videListJson)
                val playSourceBean = PlaySourceBean(0, true)
                res.forEach {
                    val str = it.value.replace("'", "")
                    val arr = str.split("$")
                    if (arr.size == 3) {
                        val title = arr[0].replace("\\u", "%u")
                        playSourceBean.episodeList.add(
                            EpisodeBean(URLCodeUtil.decode(title), arr[1], arr[1])
                        )
                        playSourceBean.count += 1
                    }
                }

                val playSourceList: MutableList<PlaySourceBean> = mutableListOf()
                playSourceList.add(playSourceBean)

                return@withContext RequestState.Success(
                    PlayDetailResultBean(
                        playSourceList, BangumiDetailBean(bangumiId)
                    )
                )
            }.getOrElse {
                return@withContext RequestState.Error(it)
            }
        }
    }

    override suspend fun getPlayUrl(url: String, retryCount: Int): RequestState<String> {
        return RequestState.Success("")
    }

    override fun getCatalogUrl(url: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun getHomeContent(): RequestState<HomeResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeeklyPlayList(): RequestState<WeeklyPlayListBean> {
        TODO("Not yet implemented")
    }


    companion object {
        var BASE_URL = "https://www.sbdm.net"

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

fun main() {
    runBlocking {
        launch {
            val res = KudmDataSource().getPlaySource("LADM/38756")
            print(res)
        }
    }
}