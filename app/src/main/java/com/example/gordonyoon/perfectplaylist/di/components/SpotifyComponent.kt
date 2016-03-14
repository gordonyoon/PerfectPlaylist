package com.example.gordonyoon.perfectplaylist.di.components

import com.example.gordonyoon.perfectplaylist.di.modules.SpotifyModule
import com.example.gordonyoon.perfectplaylist.spotify.MyBroadcastReceiver
import com.example.gordonyoon.perfectplaylist.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(SpotifyModule::class))
interface SpotifyComponent {

    fun inject(myBroadcastReceiver: MyBroadcastReceiver)

    fun inject(mainActivity: MainActivity)
}