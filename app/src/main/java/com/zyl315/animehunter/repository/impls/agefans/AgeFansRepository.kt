package com.zyl315.animehunter.repository.impls.agefans

import android.webkit.WebView
import com.zyl315.animehunter.bean.age.*
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.MaxRetryException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.execption.WebViewException
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.interfaces.ISourceRepository
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.view.widget.MyWebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.ref.WeakReference
import java.net.URLDecoder
import java.util.concurrent.CountDownLatch

class AgeFansRepository : ISourceRepository {

    private val mBangumiList: MutableList<BangumiBean> = mutableListOf()
    private var totalCount = 0
    private val webClient: MyWebViewClient by lazy {
        MyWebViewClient()
    }

    override suspend fun getSearchData(keyword: String, page: Int): RequestState<SearchResultBean> {
        val url = "search?query=$keyword&page=$page"
        return getBangumi(url, page)
    }

    override suspend fun getBangumi(url: String, page: Int): RequestState<SearchResultBean> {
        return withContext(Dispatchers.IO) {
            val doc = runCatching {
                Jsoup.parse(MyOkHttpClient.getDoc(packUrl(url)))
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }
            runCatching {
                return@withContext RequestState.Success(processBangumiHtml(doc, page))
            }.onFailure {
                return@withContext RequestState.Error(it)
            }
            return@withContext RequestState.Error(Exception("Unknown Error"))
        }
    }


    override suspend fun getMoreBangumi(url: String, page: Int): RequestState<SearchResultBean> {
        val nextPageUrl = url.replace(Regex("(name|time|点击量)-(\\d)+"), "\$1-${page}")
        return getBangumi(nextPageUrl, page)
    }

    override suspend fun getCatalog(html: String): RequestState<SearchResultBean> {
        runCatching {
            val document = Jsoup.parse(html)
            val catalogList = mutableListOf<CatalogTagBean>()
            document.getElementById("search-list")?.children()?.forEach { li ->
                if (li.tagName() == "li") {
                    val tagName = li.child(0).text().replace("：", ":")
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
                    catalogList.add(catalogTagBean)
                }
            }
            val searchResultBean = processBangumiHtml(document)
            searchResultBean.catalogTagList = catalogList
            return RequestState.Success(searchResultBean)
        }.getOrElse {
            it.printStackTrace()
            return RequestState.Error(it)
        }
    }

    override suspend fun getCatalog(
        url: String,
        webView: WeakReference<WebView>
    ): RequestState<SearchResultBean> {
        val countDownLatch: CountDownLatch = CountDownLatch(1)
        var result: RequestState<SearchResultBean> =
            RequestState.Error(Exception("Unknown Error"))

        withContext(Dispatchers.Main) {
            webClient.onComplete = { html ->
                runCatching {
                    val document = Jsoup.parse(html)
                    val catalogList = mutableListOf<CatalogTagBean>()
                    document.getElementById("search-list")?.children()?.forEach { li ->
                        if (li.tagName() == "li") {
                            val tagName = li.child(0).text().replace("：", ":")
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
                            catalogList.add(catalogTagBean)
                        }
                    }
                    val searchResultBean = processBangumiHtml(document)
                    searchResultBean.catalogTagList = catalogList
                    result = RequestState.Success(searchResultBean)
                }.getOrElse {
                    it.printStackTrace()
                    result = RequestState.Error(it)
                    countDownLatch.countDown()
                }
                countDownLatch.countDown()
            }

            webClient.onError = {
                runCatching {
                    result = RequestState.Error(WebViewException())
                    countDownLatch.countDown()
                }
            }
            webView.get()?.apply {
                webViewClient = webClient
                loadUrl(url)
            }
            countDownLatch.await()
            withContext(Dispatchers.Main) {
                webClient.removeJavascriptInterface(webView.get())
                webClient.onError = null
                webClient.onComplete = null
                webView.get()?.loadUrl("")
            }
        }
        return result
    }

    override suspend fun getPlaySource(bangumiId: String): RequestState<List<PlaySourceBean>> {
        return withContext(Dispatchers.IO) {
            val url = packUrl("/play/${bangumiId}")
            val doc = runCatching {
                Jsoup.parse(MyOkHttpClient.getDoc(url))
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }

            runCatching {
                val playSourceList: MutableList<PlaySourceBean> = mutableListOf()
                val defaultPlayIndex = doc.getElementById("DEF_PLAYINDEX").run {
                    return@run this?.data()?.toInt() ?: 0
                }
                doc.select("div#main0 div.movurl").forEachIndexed { index, element ->
                    val li = element.getElementsByTag("a")
                    if (li.size > 0) {
                        val sourceBean = PlaySourceBean(li.size, index == defaultPlayIndex)
                        li.forEach { a ->
                            sourceBean.episodeList.add(
                                EpisodeBean(a.attr("title"), a.attr("href"))
                            )
                        }
                        playSourceList.add(sourceBean)
                    }
                }
                return@withContext RequestState.Success(playSourceList)
            }.onFailure {
                return@withContext RequestState.Error(it)
            }
            return@withContext RequestState.Error(Exception("Unknown Error"))
        }
    }

    override suspend fun getPlayUrl(url: String, retryCount: Int): RequestState<String> {
        return withContext(Dispatchers.IO) {
            val playUrl =
                url.replace(Regex(".*\\/play\\/(\\d+?)\\?playid=(\\d+)_(\\d+).*"), PLAY_URL)
            runCatching {
                val body = MyOkHttpClient.getDoc(packUrl(playUrl), mapOf("Referer" to BASE_URL))
                if (body == "err:timeout") {
                    if (retryCount > 0) {
                        return@withContext getPlayUrl(url, retryCount - 1)
                    } else {
                        throw MaxRetryException()
                    }
                }

                if (body.matches("\"^ipchk:(.+)\"".toRegex())) {
                    throw IPCheckException()
                }

                val vurl = URLDecoder.decode(JSONObject(body).getString("vurl"))

                if (!vurl.contains("http")) {
                    throw UnSupportPlayTypeException()
                }

                return@withContext RequestState.Success(vurl)
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }
        }
    }

    private fun processBangumiHtml(doc: Element, page: Int = 1): SearchResultBean {
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
        return SearchResultBean(
            totalCount,
            page,
            totalCount == mBangumiList.size,
            mBangumiList
        )
    }

    companion object {
        var BASE_URL = "https://www.agefans.vip"
        var PLAY_URL = "/_getplay?aid=\$1&playindex=\$2&epindex=\$3"

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