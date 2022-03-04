package com.zyl315.animehunter.repository.interfaces

import android.webkit.WebView
import com.zyl315.animehunter.bean.age.HomeResultBean
import com.zyl315.animehunter.bean.age.PlayDetailResultBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.bean.age.SearchResultBean
import com.zyl315.animehunter.ui.widget.MyWebViewClient
import java.lang.ref.WeakReference

sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val throwable: Throwable? = null, val msg: String? = null) :
        RequestState<Nothing>()

    inline fun success(block: (Success<T>) -> Unit): RequestState<T> {
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


interface ISearchParser {
    suspend fun getSearchData(keyword: String, page: Int): RequestState<SearchResultBean>

    suspend fun getBangumi(url: String, page: Int): RequestState<SearchResultBean>

    suspend fun getMoreBangumi(url: String, page: Int): RequestState<SearchResultBean>

    suspend fun getCatalog(html: String): RequestState<SearchResultBean>

    suspend fun getCatalog(
        url: String,
        webView: WeakReference<WebView>,
        webClient: MyWebViewClient
    ): RequestState<SearchResultBean>
}

interface IPlayParser {
    suspend fun getPlaySource(bangumiId: String): RequestState<PlayDetailResultBean>

    suspend fun getPlayUrl(url: String, retryCount: Int = 3): RequestState<String>

    fun getCatalogUrl(url: String): String
}


interface IHomeParser {
    suspend fun getHomeContent():RequestState<HomeResultBean>
//    suspend fun getDailyRecommendation(): RequestState<SearchResultBean>
//
//    suspend fun getLatestUpdate(): RequestState<SearchResultBean>
//
//    suspend fun getWeeklyPlayList(): RequestState<SearchResultBean>
}