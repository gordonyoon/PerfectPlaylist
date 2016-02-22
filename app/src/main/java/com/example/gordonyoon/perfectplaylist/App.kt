package com.example.gordonyoon.perfectplaylist

import android.app.Application
import android.content.Context


class App: Application() {

    fun Context.getAppContext(): App = applicationContext as App
}