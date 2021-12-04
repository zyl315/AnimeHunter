package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.interfaces.ICatalogRepository
import okhttp3.Request

class CatalogRepository : ICatalogRepository {
    override fun getCatalog(html: String): List<CatalogTagBean> {
        return Util.processCatalog(html)
    }

    override fun getBangumi(html: String): Pair<MutableList<BangumiBean>, String> {
        return Util.processBangumi(html)
    }

    override fun getMoreBangumi(url: String): Pair<MutableList<BangumiBean>, String> {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = MyOkHttpClient.client.newCall(request).execute()
        return Util.processBangumi(response.body?.string() ?: "")
    }

}