package com.zyl315.animehunter.net.okhttp

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response

object MyOkHttpClient {
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .cookieJar(MyCookieJar())
            .build()
    }

    fun getDoc(url: String, header: Map<String, String>? = null, isGBK: Boolean = false): String {
        return if (isGBK) {
            java.lang.String(getResponse(url, header).body!!.bytes(), "GBK").toString()
        } else {
            getResponse(url, header).body!!.string()
        }

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