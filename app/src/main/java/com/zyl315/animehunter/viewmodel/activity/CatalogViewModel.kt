package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.bean.age.SearchResultBean
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {
    val dataSource: AbstractDataSource = DataSourceManager.getDataSource(DataSourceManager.DataSource.YSJDM)
    var catalogUrl = dataSource.getDefaultCatalogUrl()

    var catalogList: MutableLiveData<List<CatalogTagBean>> = MutableLiveData()
    var refreshCatalogList: List<CatalogTagBean> = mutableListOf()

    var mBangumiCoverList: MutableLiveData<List<BangumiCoverBean>> = MutableLiveData()

    var nextPage = 1

    val catalogState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()
    val bangumiState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()

    fun getCatalog(html: String) {
        viewModelScope.launch {
            catalogState.value = dataSource.getCatalog(html, nextPage).success {
                refreshCatalogList = it.data.catalogTagList!!
                if (catalogList.value == null) {
                    catalogList.postValue(refreshCatalogList)
                }
                nextPage = it.data.currentPage + 1
            }
        }
    }

    fun getMoreBangumi() {
        viewModelScope.launch {
            bangumiState.value = dataSource.getMoreBangumi(catalogUrl, nextPage).success {
                    nextPage = it.data.currentPage + 1
                }
        }
    }

    fun getCatalogUrl(catalogTagPosition: Int, tabItemPosition: Int): String {
        nextPage = 1
        catalogUrl = if (refreshCatalogList.isEmpty()) {
            catalogUrl
        } else {
            dataSource.getCatalogUrl(refreshCatalogList[catalogTagPosition].catalogItemBeanList[tabItemPosition].href)
        }
        return catalogUrl
    }
}