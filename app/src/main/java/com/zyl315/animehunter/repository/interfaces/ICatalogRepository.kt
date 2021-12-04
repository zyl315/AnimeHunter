package com.zyl315.animehunter.repository.interfaces

import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.CatalogTagBean

interface ICatalogRepository : IBaseRepository {
    fun getCatalog(html: String): List<CatalogTagBean>

    fun getBangumi(html: String): Pair<MutableList<BangumiBean>, String>

    fun getMoreBangumi(url: String): Pair<MutableList<BangumiBean>, String>

    companion object {
        const val TAG = "CatalogRepository"
    }
}