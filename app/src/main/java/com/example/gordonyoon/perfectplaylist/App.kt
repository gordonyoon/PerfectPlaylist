package com.example.gordonyoon.perfectplaylist

import android.app.Application
import com.example.gordonyoon.perfectplaylist.di.components.DaggerSpotifyComponent
import com.example.gordonyoon.perfectplaylist.di.components.SpotifyComponent
import com.example.gordonyoon.perfectplaylist.di.modules.SpotifyModule
import timber.log.Timber

class App : Application() {
    val spotifyComponent: SpotifyComponent by lazy {
        DaggerSpotifyComponent.builder().spotifyModule(SpotifyModule()).build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

    }
}