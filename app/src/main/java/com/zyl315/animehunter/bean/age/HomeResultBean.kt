package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BaseBean

class HomeResultBean(
    val contentList: List<HomeContentBean>

) : BaseBean

class HomeContentBean(
    val name: String,
    val bangumiBeanList: MutableList<BangumiBean>
) : BaseBean

class WeeklyPlayListBean(
    val weeklyPlayListMap:MutableMap<Int, MutableList<BangumiWeekListBean>>
)

