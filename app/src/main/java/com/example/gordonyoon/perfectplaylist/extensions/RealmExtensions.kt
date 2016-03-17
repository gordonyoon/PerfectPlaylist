package com.example.gordonyoon.perfectplaylist.extensions

import io.realm.Realm

fun Realm.executeAndClose(run: Realm.() -> Unit) {
    beginTransaction()
    run()
    commitTransaction()
    close()
}
