package com.zyl315.animehunter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.model.impls.agefans.SearchModel
import com.zyl315.animehunter.model.interfaces.ISearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val searchModel: ISearchModel by lazy {
        SearchModel()
    }

    fun getSearchData(keyWord: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            searchModel.getSearchData(keyWord, page)
        }
    }
}