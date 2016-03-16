package com.example.gordonyoon.perfectplaylist.di.components

import com.example.gordonyoon.perfectplaylist.di.modules.AppModule
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(nowPlayingReceiver: NowPlayingReceiver)

    fun rxBus(): RxBus
}