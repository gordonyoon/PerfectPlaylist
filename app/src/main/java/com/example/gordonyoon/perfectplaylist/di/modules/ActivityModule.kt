package com.example.gordonyoon.perfectplaylist.di.modules

import android.app.Activity
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import kaaes.spotify.webapi.android.SpotifyApi

@Module
class ActivityModule(val activity: Activity) {

    @Provides
    @PerActivity
    fun provideActivity(): Activity = activity

    @Provides
    @PerActivity
    fun provideSpotifyApi(): SpotifyApi = SpotifyApi()
}