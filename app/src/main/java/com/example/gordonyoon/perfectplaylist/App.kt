package com.example.gordonyoon.perfectplaylist

import android.app.Application
import com.example.gordonyoon.perfectplaylist.di.HasComponent
import com.example.gordonyoon.perfectplaylist.di.components.AppComponent
import com.example.gordonyoon.perfectplaylist.di.components.DaggerAppComponent
import com.example.gordonyoon.perfectplaylist.di.modules.AppModule
import timber.log.Timber

class App : Application(), HasComponent<AppComponent> {

    override val component: AppComponent
        get() = DaggerAppComponent.builder().appModule(AppModule()).build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}