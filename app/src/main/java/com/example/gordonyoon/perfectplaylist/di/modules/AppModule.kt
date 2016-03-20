package com.example.gordonyoon.perfectplaylist.di.modules

import com.example.gordonyoon.perfectplaylist.App
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import dagger.Module
import dagger.Provides
import kaaes.spotify.webapi.android.SpotifyApi
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    @Provides
    @Singleton
    fun provideApp(): App = app

    @Provides
    @Singleton
    fun provideRxBus(): RxBus = RxBus()

    @Provides
    @Singleton
    fun provideSpotifyApi(): SpotifyApi = SpotifyApi()
}