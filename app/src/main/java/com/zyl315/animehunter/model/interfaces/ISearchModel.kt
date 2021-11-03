package com.zyl315.animehunter.model.interfaces

import com.zyl315.animehunter.bean.AgeBangumiBean
import com.zyl315.animehunter.bean.BangumiCoverBean

interface ISearchModel : IBase {
    suspend fun getSearchData(keyWord: String, page: Int): List<AgeBangumiBean>

    companion object {
        const val impName = "SearchModel"
    }
}