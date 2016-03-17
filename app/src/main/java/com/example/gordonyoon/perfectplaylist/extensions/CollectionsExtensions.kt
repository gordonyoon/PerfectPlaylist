package com.example.gordonyoon.perfectplaylist.extensions

import java.util.*

/**
 * Returns a list containing all elements that are distinct by the key returned by [selector]
 * that are contained by this set and not contained by the specified collection.
 */
fun <T, K> Iterable<T>.subtractWithBy(other: Iterable<T>, selector: (T) -> K): List<T> {
    val result = ArrayList<T>()
    val restricted = HashSet<K>(other.map(selector))
    for (e in this) {
        val key = selector(e)
        if (restricted.add(key))
            result.add(e)
    }
    return result
}