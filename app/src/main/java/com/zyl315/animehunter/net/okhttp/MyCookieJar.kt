package com.zyl315.animehunter.net.okhttp

import com.zyl315.animehunter.util.AgeFans
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class MyCookieJar : CookieJar {

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return CookieStore.get(url)
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            CookieStore.add(url, cookie)
        }
        AgeFans.setCookie(url)
    }
}