package com.zyl315.animehunter.repository.impls.ysjdm

import android.webkit.WebView
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.*
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.MaxRetryException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSource
import com.zyl315.animehunter.repository.impls.kudm.KudmDataSource
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.ui.widget.MyWebViewClient
import com.zyl315.animehunter.util.URLCodeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.net.URLDecoder

class YsjdmDataSource : AbstractDataSource() {
    private val mBangumiCoverBeanList: MutableList<BangumiCoverBean> = mutableListOf()

    override fun getHost(): String {
        return BASE_URL
    }

    override fun getDefaultCatalogUrl(): String {
        TODO("Not yet implemented")
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

                doc.select("li.searchlist_item").forEach {
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
        TODO("Not yet implemented")
    }

    override suspend fun getCatalog(html: String): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getCatalog(
        url: String, webView: WeakReference<WebView>, webClient: MyWebViewClient
    ): RequestState<SearchResultBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaySource(bangumiId: String): RequestState<PlayDetailResultBean> {
        val url = KudmDataSource.packUrl("/index.php/vod/detail?id=$bangumiId")
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
                return@withContext RequestState.Success(playUrl)
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }
        }
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
        var BASE_URL = "https://www.ysjdm.net"

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
            val res = YsjdmDataSource().getPlaySource("1657")
            print(res)
        }
    }
}