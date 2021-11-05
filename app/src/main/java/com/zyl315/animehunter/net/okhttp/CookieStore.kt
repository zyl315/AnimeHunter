package com.zyl315.animehunter.net.okhttp

import okhttp3.Cookie
import okhttp3.HttpUrl

object CookieStore {
    const val TAG = "CookieStore"

    private var cookies: MutableMap<String, MutableMap<String, Cookie>> = mutableMapOf()

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = "${cookie.name}@${cookie.domain}"

        if (!cookies.containsKey(url.host)) {
            cookies[url.host] = mutableMapOf()
        }

        cookies[url.host]?.set(name, cookie)
    }

    fun get(url: HttpUrl): List<Cookie> {
        val cookieList: MutableList<Cookie> = mutableListOf()
        cookies[url.host]?.values?.let { cookieList.addAll(it) }
        return cookieList
    }
}