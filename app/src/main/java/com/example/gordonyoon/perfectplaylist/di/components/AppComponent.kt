package com.example.gordonyoon.perfectplaylist.di.components

import com.example.gordonyoon.perfectplaylist.di.modules.AppModule
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver
import com.example.gordonyoon.perfectplaylist.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(nowPlayingReceiver: NowPlayingReceiver)
//    fun inject(mainActivity: MainActivity)

    fun rxBus(): RxBus
}