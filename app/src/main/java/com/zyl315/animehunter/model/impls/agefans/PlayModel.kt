package com.zyl315.animehunter.model.impls.agefans

import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.MaxRetryException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.model.interfaces.IPlayModelModel
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.util.AgeFans.ipCheck
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLDecoder

class PlayModel : IPlayModelModel {
    private val client: OkHttpClient = MyOkHttpClient.client

    override suspend fun getPlaySource(bangumiId: String): MutableList<PlaySourceBean> {
        val playSourceList: MutableList<PlaySourceBean> = mutableListOf()

        val response = client.newCall(
            Request.Builder()
                .url(Const.AgeFans.DETAIL_URL.format(bangumiId))
                .build()
        ).execute()

        val document = Jsoup.parse(response.body?.string())

        val defaultPlayIndex = document.getElementById("DEF_PLAYINDEX").run {
            if (this != null) {
                return@run this.data().toInt()
            }
            0
        }

        document.getElementById("main0")?.getElementsByClass("movurl")
            ?.forEachIndexed() { index, it ->
                val li = it.getElementsByTag("a")
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

        return playSourceList
    }


    override suspend fun getPlayUrl(url: String, retryCount: Int): String {
        val playUrl = url.replace(
            Regex(".*\\/play\\/(\\d+?)\\?playid=(\\d+)_(\\d+).*"),
            Const.AgeFans.PLAY_URL
        )

        val request = Request.Builder()
            .url(Const.AgeFans.BASE_URL + playUrl + "&r=${Math.random()}")
            .addHeader("Referer", Const.AgeFans.BASE_URL + url)
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string() ?: ""
        if (body == "err:timeout") {
            if (retryCount > 0) {
                return getPlayUrl(url, retryCount - 1)
            } else {
                throw MaxRetryException("Exceeded the maximum number of reconnections.")
            }
        }

        if (ipCheck(body)) {
            throw IPCheckException("IP access is banned due to frequent requests.")
        }

        val jsonObj = JSONObject(body)
        val purl = jsonObj.getString("purl")
        val vurl = jsonObj.getString("vurl")
        val playEx = jsonObj.get("ex")
        val playId = jsonObj.getString("playid")

        if (!playId.contains("<play>web_mp4")) {
            throw UnSupportPlayTypeException()
        }

        return URLDecoder.decode(vurl)
    }
}

