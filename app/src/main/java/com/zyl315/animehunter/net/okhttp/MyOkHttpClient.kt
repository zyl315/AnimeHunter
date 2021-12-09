package com.zyl315.animehunter.net.okhttp

import okhttp3.OkHttpClient
import okhttp3.Request

object MyOkHttpClient {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(MyCookieJar())
            .build()
    }

    fun getDoc(url: String): String {
        return client.newCall(
            Request.Builder().url(url).build()
        ).execute().body!!.string()
    }

}