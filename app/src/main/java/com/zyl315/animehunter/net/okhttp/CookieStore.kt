package com.zyl315.animehunter.net.okhttp

import com.zyl315.animehunter.api.Const
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CookieStore {
    const val TAG = "CookieStore"

    private var cookies: MutableMap<String, MutableMap<String, Cookie>> = ConcurrentHashMap()

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getToken(cookie)

        if (!cookies.containsKey(url.host)) {
            cookies[url.host] = ConcurrentHashMap()
        }

        cookies[url.host]?.set(name, cookie)
    }

    fun get(url: HttpUrl): List<Cookie> {
        val cookieList: MutableList<Cookie> = mutableListOf()
        cookies[url.host]?.values?.let { cookieList.addAll(it) }
        return cookieList
    }

    private fun getToken(cookie: Cookie): String {
        return "${cookie.name}@${cookie.domain}"
    }

    fun createCookie(name: String, value: String, days: Int = 1): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .domain(Const.AgeFans.BASE_URL.replaceFirst("https://", ""))
            .expiresAt(System.currentTimeMillis() + days * 24 * 60 * 60 * 1000)
            .build()
    }
}