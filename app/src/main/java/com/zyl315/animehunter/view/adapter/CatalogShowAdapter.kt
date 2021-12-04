package com.zyl315.animehunter.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.CatalogShowBean
import com.zyl315.animehunter.view.activity.CatalogActivity
import com.zyl315.animehunter.view.adapter.holder.BangumiShowViewHolder
import com.zyl315.animehunter.view.adapter.holder.CatalogShowViewHolder
import com.zyl315.animehunter.view.adapter.holder.EmptyViewHolder
import com.zyl315.animehunter.view.adapter.holder.ViewHolderType

class CatalogShowAdapter(
    val activity: CatalogActivity,
    private val dataList: MutableList<CatalogShowBean>) :
    BaseRvAdapter(dataList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewHolderType.CATALOG_SHOW -> {
                CatalogShowViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_catalog_show_recycle_view, parent, false)
                )
            }
            ViewHolderType.BANGUMI_SHOW -> {
                BangumiShowViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bangumi_show_recycle_view, parent, false)
                )
            }
            else -> EmptyViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        when (holder) {
            is CatalogShowViewHolder -> {
                holder.rvCatalogShow.layoutManager = LinearLayoutManager(activity)
                holder.rvCatalogShow.adapter = activity.catalogAdapter
            }

            is BangumiShowViewHolder -> {
                holder.rvBangumiShow.layoutManager = LinearLayoutManager(activity)
                holder.smartRefresh.setEnableRefresh(false)
            }
        }
    }



    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewHolderType.CATALOG_SHOW
            1 -> ViewHolderType.BANGUMI_SHOW
            else -> super.getItemViewType(position)
        }
    }
}