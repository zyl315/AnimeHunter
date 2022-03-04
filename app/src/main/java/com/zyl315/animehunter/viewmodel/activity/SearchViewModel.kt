package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.age.SearchResultBean
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val dataSource  = DataSourceManager.getDataSource()

    val searchState: MutableLiveData<RequestState<SearchResultBean>> = MutableLiveData()
    var pageNumber: Int = 1
    var searchWord = ""

    fun getSearchData(keyWord: String, page: Int = 1) {
        if (keyWord.isBlank() || page < 0) return
        searchWord = keyWord
        pageNumber = page
        viewModelScope.launch {
            searchState.value = dataSource.getSearchData(searchWord, pageNumber)
        }
    }


    fun loadMoreData() {
        getSearchData(searchWord, pageNumber + 1)
    }
}