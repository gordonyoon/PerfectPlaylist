package com.example.gordonyoon.perfectplaylist.extensions

import io.realm.Realm

fun Realm.runAndClose(run: Realm.() -> Unit) {
    beginTransaction()
    run()
    commitTransaction()
    close()
}
