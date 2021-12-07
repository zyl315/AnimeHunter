package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.repository.interfaces.ICatalogRepository
import com.zyl315.animehunter.repository.interfaces.IPlayRepository
import com.zyl315.animehunter.repository.interfaces.ISearchRepository

class AgeFansParser : ISearchRepository, ICatalogRepository, IPlayRepository {
    override fun getCatalog(html: String): List<CatalogTagBean> {
        TODO("Not yet implemented")
    }

    override fun getBangumi(html: String): Pair<MutableList<BangumiBean>, String> {
        TODO("Not yet implemented")
    }

    override fun getMoreBangumi(url: String): Pair<MutableList<BangumiBean>, String> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaySource(bangumiId: String): MutableList<PlaySourceBean> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayUrl(url: String, retryCount: Int): String {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayUrl(episodeBean: EpisodeBean, retryCount: Int): EpisodeBean {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchData(
        keyWord: String,
        page: Int
    ): Pair<MutableList<BangumiBean>, String> {
        TODO("Not yet implemented")
    }
}