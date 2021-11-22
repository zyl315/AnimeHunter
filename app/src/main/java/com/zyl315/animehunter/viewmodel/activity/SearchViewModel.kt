package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.SearchStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.execption.NoModeDataException
import com.zyl315.animehunter.repository.impls.agefans.SearchRepository
import com.zyl315.animehunter.repository.interfaces.ISearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val searchRepository: ISearchRepository = SearchRepository()

    var mSearchResultList: MutableList<BangumiBean> = mutableListOf()
    var mSearchResultSearchStatus: MutableLiveData<SearchStatus> = MutableLiveData()
    var totalCount: String = "0"
    var pageNumber: Int = 1
    var searchWord = ""

    fun getSearchData(keyWord: String, refresh: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (refresh) {
                    pageNumber = 1
                    mSearchResultList.clear()
                }

                searchRepository.getSearchData(keyWord, pageNumber).also {
                    mSearchResultList.addAll(it.first)
                    totalCount = "共${it.second}"
                    searchWord = keyWord
                }
                mSearchResultSearchStatus.postValue(SearchStatus.SUCCESS)

            } catch (e: Exception) {
                mSearchResultSearchStatus.postValue(if (refresh) SearchStatus.REFRESH_FAILED else SearchStatus.FAILED)
                e.printStackTrace()
            }

        }
    }


    fun loadMoreData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (searchWord.isBlank()) return@launch
                searchRepository.getSearchData(searchWord, pageNumber + 1).also {
                    mSearchResultList.addAll(it.first)
                    pageNumber += 1
                }
                mSearchResultSearchStatus.postValue(SearchStatus.LOAD_MORE_SUCCESS)
            } catch (e: NoModeDataException) {
                mSearchResultSearchStatus.postValue(SearchStatus.NO_MORE_DATA)
            } catch (e: Exception) {
                mSearchResultSearchStatus.postValue(SearchStatus.LOAD_MORE_FAILED)
                e.printStackTrace()
            }
        }
    }
}