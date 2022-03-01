package com.zyl315.animehunter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.ui.adapter.holder.CatalogViewHolder

class CatalogAdapter : ListAdapter<CatalogTagBean, CatalogViewHolder>(DIFF_CALLBACK) {
    var onTabItemSelectedListener: OnTabItemSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        return CatalogViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_catalog_tab, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val item = getItem(position)
        holder.tabName.text = item.name
        holder.tabLayout.apply {
            removeAllTabs()
            item.catalogItemBeanList.forEach {
                addTab(newTab().setText(it.name), it.isSelected)
            }
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        onTabItemSelectedListener?.onTabItemSelected(
                            holder.bindingAdapterPosition,
                            tab.position
                        )
                        item.catalogItemBeanList[tab.position].isSelected = true
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.let {
                        item.catalogItemBeanList[tab.position].isSelected = false
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //No implementation required
                }

            })
        }
    }

    interface OnTabItemSelectedListener {
        fun onTabItemSelected(catalogTagPosition: Int, tabItemPosition: Int)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CatalogTagBean>() {
            override fun areItemsTheSame(
                oldItem: CatalogTagBean,
                newItem: CatalogTagBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CatalogTagBean,
                newItem: CatalogTagBean
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}