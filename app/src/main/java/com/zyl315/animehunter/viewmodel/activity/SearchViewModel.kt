package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.SearchStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.model.impls.agefans.SearchModel
import com.zyl315.animehunter.model.interfaces.ISearchModelModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val searchModel: ISearchModelModel by lazy {
        SearchModel()
    }

    var mSearchResultList: MutableList<BangumiBean> = mutableListOf()
    var mSearchResultSearchStatus: MutableLiveData<SearchStatus> = MutableLiveData()
    var totalCount: String = "0"
    var pageNumber: Int = 1
    var searchWord = ""

    fun getSearchData(keyWord: String, refresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (refresh) mSearchResultList.clear()

                searchModel.getSearchData(keyWord, pageNumber).also {
                    mSearchResultList.addAll(it.first)
                    totalCount = "共${it.second}"
                    searchWord = keyWord
                    mSearchResultSearchStatus.postValue(SearchStatus.Success)
                }

            } catch (e: Exception) {
                mSearchResultSearchStatus.postValue(SearchStatus.Failed)
                e.printStackTrace()
            }

        }
    }


    fun loadMoreData(keyWord: String) {

    }
}