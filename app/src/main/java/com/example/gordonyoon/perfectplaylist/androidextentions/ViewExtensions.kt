package com.example.gordonyoon.perfectplaylist.androidextentions

import android.support.design.widget.Snackbar
import android.view.View

fun View.snackbar(text: String,
                  duration: Int = Snackbar.LENGTH_LONG,
                  actionId: Int? = null,
                  listener: ((View) -> Unit)? = null) {
    if (actionId != null) {
        Snackbar.make(this, text, duration).setAction(actionId, listener).show()
    }
    Snackbar.make(this, text, duration).setAction(null, listener).show()
}