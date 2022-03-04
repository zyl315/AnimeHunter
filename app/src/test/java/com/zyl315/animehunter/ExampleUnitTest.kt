package com.zyl315.animehunter

import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun homePage() {
        runBlocking {
            val res = AgeFansDataSource().getPlaySource("20210169")
            println(res)
        }
    }
}