package com.example.gordonyoon.perfectplaylist.extensions

import android.support.design.widget.Snackbar

fun Snackbar.setOnDismissedNoActionCallback(run: (() -> Unit)): Snackbar {
    setCallback(object: Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar?, event: Int) {
            if (event != DISMISS_EVENT_ACTION) {
                run()
            }
        }
    })
    return this
}