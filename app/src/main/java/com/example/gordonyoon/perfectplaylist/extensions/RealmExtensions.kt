package com.example.gordonyoon.perfectplaylist.extensions

import io.realm.Realm

fun Realm.tryCommitClose(run: Realm.() -> Unit) {
    try {
        beginTransaction()
        run()
        commitTransaction()
    } finally {
        close()
    }
}