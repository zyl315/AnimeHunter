package com.zyl315.animehunter.util

object URLCodeUtil {
    fun encode(src: String): String {
        var j: Char
        val tmp = StringBuilder()
        tmp.ensureCapacity(src.length * 6)
        var i: Int = 0
        while (i < src.length) {
            j = src[i]
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j)) {
                tmp.append(j)
            } else if (j.code < 256) {
                tmp.append("%")
                if (j.code < 16) tmp.append("0")
                tmp.append(j.code.toString(16))
            } else {
                tmp.append("%u")
                tmp.append(j.code.toString(16))
            }
            i++
        }
        return tmp.toString()
    }

    fun decode(src: String): String {
        val tmp = StringBuilder()
        tmp.ensureCapacity(src.length)
        var lastPos = 0
        var pos: Int
        var ch: Char
        while (lastPos < src.length) {
            pos = src.indexOf("%", lastPos)
            if (pos == lastPos) {
                if (src[pos + 1] == 'u') {
                    ch = src.substring(pos + 2, pos + 6).toInt(16).toChar()
                    tmp.append(ch)
                    lastPos = pos + 6
                } else {
                    ch = src.substring(pos + 1, pos + 3).toInt(16).toChar()
                    tmp.append(ch)
                    lastPos = pos + 3
                }
            } else {
                lastPos = if (pos == -1) {
                    tmp.append(src.substring(lastPos))
                    src.length
                } else {
                    tmp.append(src, lastPos, pos)
                    pos
                }
            }
        }
        return tmp.toString()
    }

    /**
     * @disc 对字符串重新编码
     * @param src
     * @return
     */
    fun isoToGB(src: String): String? {
        var strRet: String? = null
        try {
            strRet = String(src.toByteArray(charset("ISO_8859_1")), charset("GB2312"))
        } catch (e: Exception) {
        }
        return strRet
    }

    /**
     * @disc 对字符串重新编码
     * @param src
     * @return
     */
    fun isoToUTF(src: String): String? {
        var strRet: String? = null
        try {
            strRet = String(src.toByteArray(charset("ISO_8859_1")), charset("UTF-8"))
        } catch (e: Exception) {
        }
        return strRet
    }
}