package com.zyl315.animehunter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.BaseBean
import com.zyl315.animehunter.ui.activity.CatalogActivity
import com.zyl315.animehunter.ui.adapter.holder.CatalogShowViewHolder

class CatalogShowAdapter(
    val activity: CatalogActivity,
    val dataList: List<BaseBean>
) : BaseRvAdapter(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CatalogShowViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_catalog_show_recycle_view, parent, false)
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CatalogShowViewHolder -> {
                holder.rvCatalogShow.layoutManager = LinearLayoutManager(activity)
                holder.rvCatalogShow.adapter = activity.catalogAdapter
            }
        }
    }
}