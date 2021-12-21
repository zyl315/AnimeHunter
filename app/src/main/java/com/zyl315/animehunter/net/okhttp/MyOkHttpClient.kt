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
        return getDoc(url, emptyMap())
    }

    fun getDoc(url: String, header: Map<String, String>): String {
        val builder = Request.Builder()
        header.entries.forEach {
            builder.addHeader(it.key, it.value)
        }
        builder.url(url)
        return client.newCall(builder.build()).execute().body!!.string()
    }
}