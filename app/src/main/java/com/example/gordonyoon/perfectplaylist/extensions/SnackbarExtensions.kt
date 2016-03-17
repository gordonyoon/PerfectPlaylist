package com.example.gordonyoon.perfectplaylist.extensions

import android.app.Activity
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar

fun Snackbar.make(activity: Activity, text: CharSequence, @Snackbar.Duration duration: Int): Snackbar {
    return Snackbar.make(activity.window.decorView.rootView, text, duration)
}

fun Snackbar.make(activity: Activity, @StringRes resId: Int, @Snackbar.Duration duration: Int): Snackbar {
    return Snackbar.make(activity.window.decorView.rootView, resId, duration)
}