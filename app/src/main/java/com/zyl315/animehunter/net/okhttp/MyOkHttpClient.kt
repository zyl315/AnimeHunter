package com.zyl315.animehunter.net.okhttp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object MyOkHttpClient {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(MyCookieJar())
            .build()
    }

    fun getDoc(url: String, header: Map<String, String>? = null): String {
        return getResponse(url, header).body!!.string()
    }

    fun getResponse(url: String, header: Map<String, String>? = null): Response {
        val builder = Request.Builder()
        header?.entries?.forEach {
            builder.addHeader(it.key, it.value)
        }
        builder.url(url)
        return client.newCall(builder.build()).execute()
    }
}