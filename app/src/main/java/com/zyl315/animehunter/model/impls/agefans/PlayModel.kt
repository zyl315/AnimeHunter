package com.zyl315.animehunter.model.impls.agefans

import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlayUrlBean
import com.zyl315.animehunter.model.interfaces.IPlayModelModel
import com.zyl315.animehunter.net.okhttp.MyCookieJar
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException

class PlayModel : IPlayModelModel {
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(MyCookieJar())
            .build()
    }

    override suspend fun getDetailPage(bangumiId: String): MutableList<PlayUrlBean> {
        val playUrlList: MutableList<PlayUrlBean> = mutableListOf()

        val response = client.newCall(
            Request.Builder()
                .url(Const.AgeFans.DETAIL_URL.format(bangumiId))
                .build()
        ).execute()

        Jsoup.parse(response.body?.string()).apply {
            getElementById("main0")?.getElementsByClass("movurl")?.forEach {
                val li = it.getElementsByTag("a")
                if (li.size > 0) {
                    val urlBean = PlayUrlBean(li.size, it.attr("style") == "display:block")

                    li.forEach { a ->
                        urlBean.episodeList.add(
                            EpisodeBean(a.attr("title"), a.attr("href"))
                        )
                    }

                    playUrlList.add(urlBean)
                }
            }
        }
        return playUrlList
    }


    override suspend fun getPlayUrl(bangumiId: String, playIndex: Int, epIndex: Int): String {
        val request = Request.Builder()
            .url(Const.AgeFans.PLAY_URL.format(bangumiId, playIndex, epIndex, Math.random()))
            .addHeader(
                "Referer",
                Const.AgeFans.PLAY_REFERER.format(bangumiId, "${playIndex}_${epIndex}"))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                response.headers.toMultimap()
            }

        })
        return ""
    }
}