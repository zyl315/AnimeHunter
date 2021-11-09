package com.zyl315.animehunter.util

import com.zyl315.animehunter.net.okhttp.CookieStore
import okhttp3.HttpUrl
import java.util.*
import kotlin.math.roundToLong

object AgeFans {
    fun setCookie(url: HttpUrl) {
        CookieStore.get(url).forEach { cookie ->
            if (cookie.name == "t1") {
                val t1 = (cookie.value.toLong() / 1e3).roundToLong() shr 5
                val k2 = (t1 * (t1 % 4096) * 3 + 83215) * (t1 % 4096) + t1
                CookieStore.apply {
                    add(url, createCookie("k2", k2.toString()))
                }
            }
        }
        CookieStore.apply {
            add(url, createCookie("t2", Date().time.toString()))
            add(url, createCookie("fa_t", Date().time.toString()))
            add(url, createCookie("fa_c", "1"))
        }
    }


    fun ipCheck(data: String): Boolean {
        return data.matches(Regex("^ipchk:(.+)"))
    }
}