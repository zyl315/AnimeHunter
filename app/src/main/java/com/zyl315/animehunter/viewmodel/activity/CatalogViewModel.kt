package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.bean.age.SearchResultBean
import com.zyl315.animehunter.repository.impls.agefans.AgeFansRepository
import com.zyl315.animehunter.repository.interfaces.ISourceRepository
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {
    private val sourceRepository: ISourceRepository = AgeFansRepository()
    var catalogUrl = AgeFansRepository.DEFAULT_CATALOG_URL

    var catalogList: MutableLiveData<List<CatalogTagBean>> = MutableLiveData()
    var refreshCatalogList: List<CatalogTagBean> = mutableListOf()

    var bangumiList: MutableLiveData<List<BangumiBean>> = MutableLiveData()

    var nextPage = 1

    val catalogState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()
    val bangumiState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()

    fun getCatalog(html: String) {
        viewModelScope.launch {
            catalogState.value =
                sourceRepository.getCatalog(html).success {
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
            bangumiState.value = sourceRepository.getMoreBangumi(catalogUrl, nextPage)
                .success {
                    nextPage = it.data.currentPage + 1
                }
        }
    }

    fun getCatalogUrl(catalogTagPosition: Int, tabItemPosition: Int): String {
        catalogUrl = if (refreshCatalogList.isEmpty()) {
            catalogUrl
        } else {
            sourceRepository.getCatalogUrl(refreshCatalogList[catalogTagPosition].catalogItemBeanList[tabItemPosition].href)
        }
        return catalogUrl
    }
}