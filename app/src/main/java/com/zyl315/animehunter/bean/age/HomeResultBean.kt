package com.zyl315.animehunter.bean.age

import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.BaseBean

class HomeResultBean(
    val contentList: List<HomeContentBean>

) : BaseBean

class HomeContentBean(
    val name: String,
    val bangumiCoverBeanList: MutableList<BangumiCoverBean>
) : BaseBean

class WeeklyPlayListBean(
    val weeklyPlayListMap:MutableMap<Int, MutableList<BangumiWeekListBean>>
)

