package com.zyl315.animehunter.model.interfaces

import com.zyl315.animehunter.bean.age.BangumiBean

interface ISearchModelModel : IBaseModel {
    suspend fun getSearchData(keyWord: String, page: Int): Pair<MutableList<BangumiBean>, Int>

    companion object {
        const val TAG = "SearchModel"
    }
}