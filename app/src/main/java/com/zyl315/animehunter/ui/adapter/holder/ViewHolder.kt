package com.zyl315.animehunter.ui.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R

object ViewHolderType {
    const val UNKNOWN = -1
    const val EMPTY = 0
    const val BANGUMI_COVER_1 = 10000
    const val BANGUMI_COVER_2 = 10001
    const val PLAY_SOURCE = 20000
    const val WATCH_HISTORY = 30000
}


class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class BangumiCover1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val tvType: TextView = itemView.findViewById(R.id.tv_type)
    val tvLastUpdateTime: TextView = itemView.findViewById(R.id.tv_lastUpdateTime)
    val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
}

class BangumiCover2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
}

class HomeContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val rvBangumi: RecyclerView = itemView.findViewById(R.id.rv_bangumi)
}

class PlaySourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvEpisode: TextView = itemView.findViewById(R.id.tv_episode)
}

class WatchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvBangumiTitle: TextView = itemView.findViewById(R.id.tv_bangumiTitle)
    val tvDataSource:TextView = itemView.findViewById(R.id.tv_dataSource)
    val tvEpisodeTitle: TextView = itemView.findViewById(R.id.tv_episodeTitle)
    val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
    val tvLastWatchTime: TextView = itemView.findViewById(R.id.tv_lastWatchTime)
    val rbSelect: RadioButton = itemView.findViewById(R.id.rb_select)
}

class CatalogShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val rvCatalogShow: RecyclerView = itemView.findViewById(R.id.rv_catalog_show)
}

class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    val tabName: TextView = itemView.findViewById(R.id.tv_tabName)
var tabLayout: TabLayout = itemView.findViewById(R.id.tab_layout)
}