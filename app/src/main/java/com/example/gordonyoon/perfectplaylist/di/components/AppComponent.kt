package com.example.gordonyoon.perfectplaylist.di.components

import com.example.gordonyoon.perfectplaylist.App
import com.example.gordonyoon.perfectplaylist.di.modules.AppModule
import com.example.gordonyoon.perfectplaylist.rx.RxBus
import com.example.gordonyoon.perfectplaylist.spotify.Authenticator
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingReceiver
import com.example.gordonyoon.perfectplaylist.spotify.NowPlayingState
import com.example.gordonyoon.perfectplaylist.spotify.SpotifyWidgetController
import dagger.Component
import kaaes.spotify.webapi.android.SpotifyApi
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(nowPlayingReceiver: NowPlayingReceiver)

    fun app(): App
    fun rxBus(): RxBus
    fun spotifyApi(): SpotifyApi
    fun authenticator(): Authenticator
    fun nowPlayingState(): NowPlayingState
    fun spotifyWidgetController(): SpotifyWidgetController
}