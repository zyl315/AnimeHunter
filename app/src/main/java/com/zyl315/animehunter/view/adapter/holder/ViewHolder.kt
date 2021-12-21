package com.zyl315.animehunter.view.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zyl315.animehunter.R

object ViewHolderType {
    const val UNKNOWN = -1
    const val EMPTY = 0
    const val BANGUMI_COVER_1 = 10000
    const val BANGUMI_COVER_2 = 10001
    const val PLAY_SOURCE = 20000
    const val WATCH_HISTORY = 30000
    const val CATALOG_SHOW = 40000
    const val BANGUMI_SHOW = 50000
}


class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class BangumiCover1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvNewName: TextView = itemView.findViewById(R.id.tv_newName)
    val tvName: TextView = itemView.findViewById(R.id.tv_name)
    val tvBangumiType: TextView = itemView.findViewById(R.id.tv_bangumiType)
    val tvPremiereTime: TextView = itemView.findViewById(R.id.tv_premiereTime)
    val tvPlayStatus: TextView = itemView.findViewById(R.id.tv_playStatus)
    val tvPlotType: TextView = itemView.findViewById(R.id.tv_plotType)
}

class BangumiCover2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvNewName: TextView = itemView.findViewById(R.id.tv_newName)
    val tvName: TextView = itemView.findViewById(R.id.tv_name)
}

class PlaySourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvEpisode: TextView = itemView.findViewById(R.id.tv_episode)
}

class WatchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvBangumiTitle: TextView = itemView.findViewById(R.id.tv_bangumiTitle)
    val tvEpisodeTitle: TextView = itemView.findViewById(R.id.tv_episodeTitle)
    val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
    val tvLastWatchTime: TextView = itemView.findViewById(R.id.tv_lastWatchTime)
    val rbSelect: RadioButton = itemView.findViewById(R.id.rb_select)
}

class CatalogShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val rvCatalogShow: RecyclerView = itemView.findViewById(R.id.rv_catalog_show)
}

class BangumiShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val smartRefresh: SmartRefreshLayout = itemView.findViewById(R.id.smart_refresh_layout)
    val rvBangumiShow: RecyclerView = itemView.findViewById(R.id.rv_bangumi_show)
}

class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tabName: TextView = itemView.findViewById(R.id.tv_tabName)
    val tabLayout: TabLayout = itemView.findViewById(R.id.tab_layout)
}