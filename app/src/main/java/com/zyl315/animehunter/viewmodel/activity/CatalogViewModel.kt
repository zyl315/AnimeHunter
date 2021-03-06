package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.age.BangumiDetailBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.bean.age.SearchResultBean
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {
    private val dataSource: AbstractDataSource = DataSourceManager.getDataSource()
    var catalogUrl = dataSource.getDefaultCatalogUrl()

    var catalogList: MutableLiveData<List<CatalogTagBean>> = MutableLiveData()
    var refreshCatalogList: List<CatalogTagBean> = mutableListOf()

    var mBangumiDetailList: MutableLiveData<List<BangumiDetailBean>> = MutableLiveData()

    var nextPage = 1

    val catalogState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()
    val bangumiState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()

    fun getCatalog(html: String) {
        viewModelScope.launch {
            catalogState.value =
                dataSource.getCatalog(html).success {
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
            bangumiState.value = dataSource.getMoreBangumi(catalogUrl, nextPage)
                .success {
                    nextPage = it.data.currentPage + 1
                }
        }
    }

    fun getCatalogUrl(catalogTagPosition: Int, tabItemPosition: Int): String {
        catalogUrl = if (refreshCatalogList.isEmpty()) {
            catalogUrl
        } else {
            dataSource.getCatalogUrl(refreshCatalogList[catalogTagPosition].catalogItemBeanList[tabItemPosition].href)
        }
        return catalogUrl
    }
}