package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

data class CatalogShowBean(
    val list: MutableList<out BaseBean>
) : BaseBean