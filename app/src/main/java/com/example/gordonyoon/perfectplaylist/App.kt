package com.example.gordonyoon.perfectplaylist

import android.app.Application
import com.example.gordonyoon.perfectplaylist.di.components.AppComponent
import com.example.gordonyoon.perfectplaylist.di.components.DaggerAppComponent
import com.example.gordonyoon.perfectplaylist.di.modules.AppModule
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule()).build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

    }
}