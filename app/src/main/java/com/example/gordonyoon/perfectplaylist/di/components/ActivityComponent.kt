package com.example.gordonyoon.perfectplaylist.di.components

import android.app.Activity
import com.example.gordonyoon.perfectplaylist.di.modules.ActivityModule
import com.example.gordonyoon.perfectplaylist.di.scopes.PerActivity
import com.example.gordonyoon.perfectplaylist.ui.MainActivity
import dagger.Component
import kaaes.spotify.webapi.android.SpotifyApi

@PerActivity
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun activity(): Activity
}