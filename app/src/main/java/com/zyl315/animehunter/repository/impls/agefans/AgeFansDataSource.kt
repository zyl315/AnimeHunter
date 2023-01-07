package com.zyl315.animehunter.repository.impls.agefans

import android.content.Context
import android.webkit.WebView
import com.zyl315.animehunter.App
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.*
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.MaxRetryException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.execption.WebViewException
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager.DataSource
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.ui.widget.MyWebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.ref.WeakReference
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.URLDecoder
import java.util.concurrent.CountDownLatch

class AgeFansDataSource : AbstractDataSource() {
    private val mBangumiCoverList: MutableList<BangumiCoverBean> = mutableListOf()
    private var totalCount = 0

    init {
        BASE_URL = getLocalBaseUrl()
        GlobalScope.launch {
            checkHost()
        }
    }

    override fun getHost(): String = BASE_URL

    override fun getDefaultCatalogUrl(): String = DEFAULT_CATALOG_URL

    override suspend fun checkHost() {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val response = MyOkHttpClient.getResponse(REFERRAL_URL)
                BASE_URL = "${response.request.url.scheme}://${response.request.url.host}"
                saveLocalBaseUrl(BASE_URL)
            }
        }
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

    override suspend fun getCatalog(html: String, page: Int): RequestState<SearchResultBean> {
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
        url: String, webView: WeakReference<WebView>, webClient: MyWebViewClient
    ): RequestState<SearchResultBean> {
        val countDownLatch: CountDownLatch = CountDownLatch(1)
        var result: RequestState<SearchResultBean> = RequestState.Error(Exception("Unknown Error"))

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
                webClient.removeJavascriptInterface()
                webClient.onError = null
                webClient.onComplete = null
                webView.get()?.loadUrl("")
            }
        }
        return result
    }

    override suspend fun getPlaySource(bangumiId: String): RequestState<PlayDetailResultBean> {
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
                val infoList = mutableListOf<String>()
                doc.select("#play_imform li span.play_imform_val").forEach {
                    infoList.add(it.text())
                }

                val img = doc.select("#container > div:nth-child(5) div.blockcontent table a img")
                val name = img.attr("alt")
                val src = packUrl(img.attr("src"))

                val desc = doc.select("div.play_desc p")[0].text()

                val bangumiDetailBean = BangumiDetailBean(bangumiId).apply {
                    this.name = name
                    this.coverUrl = src
                    this.newName = ""
                    this.bangumiType = infoList[1]
                    this.originalWork = infoList[2]
                    this.originalName = infoList[3]
                    this.otherName = infoList[4]
                    this.productionCompany = infoList[5]
                    this.premiereTime = infoList[6]
                    this.playStatus = infoList[7]
                    this.plotType = infoList[8]
                    this.description = desc
                }

                return@withContext RequestState.Success(
                    PlayDetailResultBean(
                        playSourceList, bangumiDetailBean
                    )
                )
            }.onFailure {
                return@withContext RequestState.Error(it)
            }
            return@withContext RequestState.Error(Exception("Unknown Error"))
        }
    }

    override suspend fun getPlayUrl(url: String, retryCount: Int): RequestState<String> {
        return withContext(Dispatchers.IO) {
            val playUrl = url.replace(Regex(".*\\/play\\/(\\d+?)\\?playid=(\\d+)_(\\d+).*"), PLAY_URL)
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

                val jsonObj = JSONObject(body)
                val purl = jsonObj.getString("purl")
                val vurl = URLDecoder.decode(jsonObj.getString("vurl"))
                val playId = jsonObj.getString("playid")

                val _url = packUrl(purl + vurl).toHttpUrl().queryParameter("url").toString()


                if (!_url.contains("http")) {
                    throw UnSupportPlayTypeException()
                }

                return@withContext RequestState.Success(URLDecoder.decode(_url))
            }.getOrElse {
                it.printStackTrace()
                return@withContext RequestState.Error(it)
            }
        }
    }

    override fun getCatalogUrl(url: String): String {
        return "$BASE_URL/catalog/$url"
    }

    override suspend fun getHomeContent(): RequestState<HomeResultBean> {
        var retryCount = 1
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(packUrl(BASE_URL)))
                val resultBean = processHomeHtml(doc)

                return@withContext RequestState.Success(resultBean)
            }.getOrElse {
                if(it is SocketTimeoutException && retryCount > 0)  {
                    retryCount--
                    getHomeContent()
                } else {
                    return@withContext RequestState.Error(it)
                }
            }
        }
    }

    override suspend fun getWeeklyPlayList(): RequestState<WeeklyPlayListBean> {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val doc = Jsoup.parse(MyOkHttpClient.getDoc(packUrl(BASE_URL)))
                val bangumiScript = doc.select("#container div.div_right.baseblock  script")[0].html()
                val res: Sequence<MatchResult> = Regex("\\{\"[^\\}]+\"\\}").findAll(bangumiScript)
                val weeklyPlayMap: MutableMap<Int, MutableList<BangumiWeekListBean>> = mutableMapOf()
                for (jsonStr in res) {
                    val jsonObject = JSONObject(jsonStr.value)
                    val bangumiWeekListBean = BangumiWeekListBean(
                        jsonObject.getString("id"),
                        jsonObject.getInt("wd"),
                        jsonObject.getString("name"),
                        jsonObject.getBoolean("isnew"),
                        jsonObject.getString("mtime"),
                        jsonObject.getString("namefornew")
                    )

                    weeklyPlayMap.let {
                        if (!it.containsKey(bangumiWeekListBean.dayOfWeek)) {
                            weeklyPlayMap[bangumiWeekListBean.dayOfWeek] = mutableListOf()
                        }
                        it[bangumiWeekListBean.dayOfWeek]!!.add(bangumiWeekListBean)
                    }
                }
                return@withContext RequestState.Success(WeeklyPlayListBean(weeklyPlayMap))
            }.getOrElse {
                return@withContext RequestState.Error(it)
            }
        }
    }

    private fun processBangumiHtml(doc: Element, page: Int = 1): SearchResultBean {
        if (page == 1) {
            mBangumiCoverList.clear()
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
//            BangumiDetailBean(bangumiId).apply {
//                this.name = title
//                this.coverUrl = coverUrl
//                this.newName = newName
//                this.bangumiType = infoList.getOrElse(0) { "" }
//                this.originalName = infoList.getOrElse(1) { "" }
//                this.otherName = infoList.getOrElse(2) { "" }
//                this.premiereTime = infoList.getOrElse(3) { "" }
//                this.playStatus = infoList.getOrElse(4) { "" }
//                this.originalWork = infoList.getOrElse(5) { "" }
//                this.plotType = infoList.getOrElse(6) { "" }
//                this.productionCompany = infoList.getOrElse(7) { "" }
//                this.description = infoList.getOrElse(8) { "" }
//            }

            mBangumiCoverList.add(
                BangumiCoverBean(DataSource.AGEFANS,
                    bangumiId,
                    title,
                    coverUrl,
                    infoList.getOrElse(0) { "" },
                    newName,
                    infoList.getOrElse(3) { "" })
            )
        }
        return SearchResultBean(
            totalCount, page, totalCount == mBangumiCoverList.size, mBangumiCoverList
        )
    }

    private fun processHomeHtml(doc: Element): HomeResultBean {
        val contentList: MutableList<HomeContentBean> = mutableListOf()
        val left = doc.select("#container > div.div_left.baseblock")[0]
        left?.select("div.blocktitle")?.forEach { element ->
            val title = element.child(0).text()
            val bangumiCoverBeanList: MutableList<BangumiCoverBean> = mutableListOf()
            element.nextElementSibling()!!.select("li.anime_icon1").forEach {
                val bangumiId = it.child(0).attr("href").replaceFirst("/detail/", "")
                val coverUrl = packUrl(it.child(0).child(0).attr("src"))
                val name = it.child(0).child(0).attr("alt")
                val status = it.child(0).child(1).text()

                bangumiCoverBeanList.add(
                    BangumiCoverBean(
                        DataSource.AGEFANS, bangumiId, name, coverUrl, "", status, ""
                    )
                )
            }
            contentList.add(
                HomeContentBean(
                    title, bangumiCoverBeanList
                )
            )

            contentList.reverse()
        }
        return HomeResultBean(contentList)
    }

    companion object {
        const val AGE_LOCAL = "age_local"
        const val AGE_LOCAL_BASE_URL = "age_local_base_url"
        var BASE_URL = "https://www.agemys.net"
        var REFERRAL_URL = "https://dx.mbn98.com/?u=http://age.tv/&p=/"
        var PLAY_URL = "/_getplay?aid=\$1&playindex=\$2&epindex=\$3"
        var DEFAULT_CATALOG_URL = "${BASE_URL}/catalog/all-all-all-all-all-time-1"

        fun packUrl(url: String): String {
            return when {
                url.startsWith("http") -> url
                url.startsWith("//") -> "https:$url"
                url.startsWith("/") -> "$BASE_URL$url"
                else -> "$BASE_URL/$url"
            }
        }

        fun getLocalBaseUrl():String {
            val sharedPreferences = App.context.getSharedPreferences(AGE_LOCAL, Context.MODE_PRIVATE)
            return sharedPreferences.getString(AGE_LOCAL_BASE_URL,  "https://www.agemys.net").toString()
        }

        fun saveLocalBaseUrl(baseUrl:String) {
            val sharedPreferences = App.context.getSharedPreferences(AGE_LOCAL, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(AGE_LOCAL_BASE_URL, baseUrl).apply()
        }
    }
}