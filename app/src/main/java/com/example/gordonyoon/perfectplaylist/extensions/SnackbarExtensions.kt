package com.example.gordonyoon.perfectplaylist.extensions

import android.support.design.widget.Snackbar

fun Snackbar.setOnDismissedCallback(onDismissed: (() -> Unit)? = null, noAction: (() -> Unit)? = null): Snackbar {
    setCallback(object: Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar?, event: Int) {
            if (event != DISMISS_EVENT_ACTION) {
                if (noAction != null) noAction()
            }
            if (onDismissed != null) onDismissed()
        }
    })
    return this
}