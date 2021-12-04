package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.net.okhttp.MyOkHttpClient
import com.zyl315.animehunter.repository.interfaces.ISearchRepository
import okhttp3.Request

class SearchRepository : ISearchRepository {

    override suspend fun getSearchData(
        keyWord: String,
        page: Int
    ): Pair<MutableList<BangumiBean>, String> {

        val request = Request.Builder()
            .url(Const.AgeFans.SEARCH_URL.format(keyWord, page))
            .build()

        val response = MyOkHttpClient.client.newCall(request).execute()

        return Util.processBangumi(response.body?.string() ?: "")
    }
}