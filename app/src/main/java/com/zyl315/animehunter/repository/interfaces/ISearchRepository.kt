package com.zyl315.animehunter.repository.interfaces

import com.zyl315.animehunter.bean.age.BangumiBean

interface ISearchRepository : IBaseRepository {
    suspend fun getSearchData(keyWord: String, page: Int): Pair<MutableList<BangumiBean>, String>

    companion object {
        const val TAG = "SearchRepository"
    }
}