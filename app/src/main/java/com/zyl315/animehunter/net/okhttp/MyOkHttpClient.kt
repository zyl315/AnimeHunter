package com.zyl315.animehunter.net.okhttp

import okhttp3.OkHttpClient

object MyOkHttpClient {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(MyCookieJar())
            .build()
    }
}