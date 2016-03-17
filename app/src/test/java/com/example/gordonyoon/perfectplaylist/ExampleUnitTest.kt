package com.example.gordonyoon.perfectplaylist

import com.example.gordonyoon.perfectplaylist.extensions.subtractWithBy
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    @Throws(Exception::class)
    fun listSplit_isCorrect() {
        val list1: List<Int> = listOf(0..11).flatten()
        assertEquals(list1.split(5), listOf(listOf(0..4), listOf(5.rangeTo(9)), listOf(10.rangeTo(11))).map { it.flatten() })
    }

    fun <E> List<E>.split(increment: Int = 1): List<List<E>> {
        val result: ArrayList<List<E>> = ArrayList()
        var start: Int = 0
        do {
            val end = if (start + increment - 1 < size) start + increment - 1 else size - 1
            result.add(slice(start..end))
            start += increment
        } while (start < size)
        return result
    }

    @Test
    @Throws(Exception::class)
    fun subtractBy_isCorrect() {
        val list1 = ArrayList<SubtractByHelper>()
        val list2 = ArrayList<SubtractByHelper>()

        for (i in 1..6) list1.add(SubtractByHelper(x = i))
        for (i in 4..6) list2.add(SubtractByHelper(x = i))

        assertEquals(3, list1.subtractWithBy(list2) { it.x }.size)
    }

    class SubtractByHelper(val x: Int = 0)
}