package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.api.SearchStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.CatalogShowBean
import com.zyl315.animehunter.bean.age.CatalogTagBean
import com.zyl315.animehunter.execption.NoModeDataException
import com.zyl315.animehunter.repository.impls.agefans.CatalogRepository
import com.zyl315.animehunter.repository.impls.agefans.SearchRepository
import com.zyl315.animehunter.repository.interfaces.ICatalogRepository
import com.zyl315.animehunter.repository.interfaces.ISearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {
    private val catalogRepository: ICatalogRepository = CatalogRepository()
    var catalogUrl = Const.AgeFans.DEFAULT_CATALOG_URL

    var catalogList: MutableLiveData<List<CatalogTagBean>> = MutableLiveData()
    var refreshCatalogList: List<CatalogTagBean> = mutableListOf()

    var bangumiList: MutableLiveData<List<BangumiBean>> = MutableLiveData()

    var totalCount = "0"
    var pageNumber = 1

    val getCatalogSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var searchResultStatus: MutableLiveData<SearchStatus> = MutableLiveData()

    fun getAllData(html: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getCatalog(html)
            getBangumi(html)
        }
    }


    private fun getCatalog(html: String) {
        try {
            refreshCatalogList = catalogRepository.getCatalog(html)
            if (catalogList.value == null) {
                catalogList.postValue(refreshCatalogList)
            }
        } catch (e: Exception) {
            getCatalogSuccess.postValue(false)
            e.printStackTrace()
        }
    }

    private fun getBangumi(html: String, refresh: Boolean = true) {
        try {
            val result = catalogRepository.getBangumi(html)
            bangumiList.postValue(result.first)
            totalCount = result.second
            pageNumber = 2
            searchResultStatus.postValue(SearchStatus.SUCCESS)
        } catch (e: Exception) {
            searchResultStatus.postValue(SearchStatus.FAILED)
            e.printStackTrace()
        }
    }

    fun getCatalogUrl(catalogTagPosition: Int, tabItemPosition: Int): String {
        catalogUrl = if (refreshCatalogList.isEmpty()) {
            Const.AgeFans.DEFAULT_CATALOG_URL
        } else {
            Const.AgeFans.CATALOG_URL + refreshCatalogList[catalogTagPosition].catalogItemBeanList[tabItemPosition].href
        }
        return catalogUrl
    }

    fun loadMoreBangumi() {
        val nextPageUrl = catalogUrl.replace(
            Regex("(name|time|%E7%82%B9%E5%87%BB%E9%87%8F)-(\\d)+"),
            "\$1-${pageNumber}"
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = catalogRepository.getMoreBangumi(nextPageUrl)
                bangumiList.postValue(bangumiList.value?.plus(result.first))
                totalCount = result.second
                pageNumber++
                searchResultStatus.postValue(SearchStatus.LOAD_MORE_SUCCESS)
            } catch (e: NoModeDataException) {
                searchResultStatus.postValue(SearchStatus.NO_MORE_DATA)
            } catch (e: Exception) {
                searchResultStatus.postValue(SearchStatus.LOAD_MORE_FAILED)
                e.printStackTrace()
            }
        }
    }
}