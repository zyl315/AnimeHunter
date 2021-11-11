package com.zyl315.animehunter.util

import com.zyl315.animehunter.net.okhttp.CookieStore
import okhttp3.HttpUrl
import java.util.*
import kotlin.math.roundToLong

object AgeFans {
    fun updateCookie(url: HttpUrl) {
        CookieStore.get(url, "t1")?.let { cookie ->
            val t1 = (cookie.value.toLong() / 1e3).roundToLong() shr 5
            val k2 = (t1 * (t1 % 4096) * 3 + 83215) * (t1 % 4096) + t1

            val ksub = k2.toString().last()
            var t2: String
            while (true) {
                t2 = Date().time.toString()
                if (t2.slice(t2.length - 1 downTo t2.length - 3).contains(ksub)) {
                    break
                }
            }
            CookieStore.apply {
                add(url, createCookie("k2", k2.toString(), cookie.domain))
                add(url, createCookie("t2", t2, cookie.domain))
                add(url, createCookie("fa_t", Date().time.toString(), cookie.domain))
                add(url, createCookie("fa_c", "1", cookie.domain))
            }
        }
    }

    fun ipCheck(data: String): Boolean {
        return data.matches(Regex("^ipchk:(.+)"))
    }
}