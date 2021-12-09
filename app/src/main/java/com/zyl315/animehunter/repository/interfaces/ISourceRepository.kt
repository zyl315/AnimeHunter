package com.zyl315.animehunter.repository.interfaces

import com.zyl315.animehunter.bean.age.SearchResultBean

sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val throwable: Throwable? = null, val msg: String? = null) :
        RequestState<Nothing>()

    inline fun complete(block: (Success<T>) -> Unit): RequestState<T> {
        if (this is Success) {
            block(this)
        }
        return this
    }

    inline fun error(block: (Error) -> Unit): RequestState<T> {
        if (this is Error) {
            block(this)
        }
        return this
    }
}

interface ISourceRepository : ISearchParser {


}

interface ISearchParser {
    suspend fun getSearchData(
        keyword: String,
        page: Int
    ): RequestState<SearchResultBean>
}