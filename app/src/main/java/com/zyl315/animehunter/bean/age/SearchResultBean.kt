package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

data class SearchResultBean(
    val totalCount: Int = 0,
    val currentPage: Int,
    val isLastPage: Boolean,
    val bangumiList: MutableList<BangumiBean>,
    var catalogTagList: List<CatalogTagBean>? = null
) : BaseBean