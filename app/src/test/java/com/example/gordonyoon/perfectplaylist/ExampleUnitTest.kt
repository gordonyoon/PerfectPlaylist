package com.example.gordonyoon.perfectplaylist

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
    fun slice_isCorrect() {
        val INC = 10
        val size = 21
        val newMinusSavedTracks = ArrayList<Int>()
        val tracks = listOf(1..21).flatten()
        var offset = 0
        do {
            val start = offset * INC
            val end = if (start + INC - 1 < size) start + INC - 1 else size - 1
            val slicedTracks = tracks.slice(start..end)
            val contains = tracks.slice(start..end).map { it % 3 == 0 }
            slicedTracks.filterIndexedTo(newMinusSavedTracks) { i, track -> contains[i] }
            offset++
        } while (start + INC < size)
        assertEquals(newMinusSavedTracks, listOf(3, 6, 9, 12, 15, 18, 21))
    }
}