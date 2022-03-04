package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

data class CatalogTagBean(
    var name: String,
    var catalogItemBeanList: MutableList<CatalogItemBean> = mutableListOf()
) : BaseBean

data class CatalogItemBean(
    var name: String,
    var href: String,
    var isSelected: Boolean
) : BaseBean

