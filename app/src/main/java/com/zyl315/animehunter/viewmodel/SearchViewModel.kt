package com.zyl315.animehunter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.Status
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
    var searchResultStatus: MutableLiveData<Status> = MutableLiveData()
    var pageNumber: Int = 1
    var searchWord = ""

    fun getSearchData(keyWord: String, refresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (refresh) mSearchResultList.clear()

                searchModel.getSearchData(keyWord, pageNumber).also {
                    mSearchResultList.addAll(it.first)
                    searchWord = keyWord
                    searchResultStatus.postValue(Status.Success)
                }

            } catch (e: Exception) {
                searchResultStatus.postValue(Status.Failed)
                e.printStackTrace()
            }

        }
    }


    fun loadMoreData(keyWord: String) {

    }
}